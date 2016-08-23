/*
 * ESP8266.cpp
 *
 *  Created on: Jul 3, 2016
 *      Author: gijs
 */

#include "ESP8266.h"
#include "Steer.h"
#include "Engine.h"
#include "../jansson/jansson.h"
#include "../Utils.h"
#include "Base64.h"
#include <syslog.h>
#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <algorithm>
#include <string>
#include <pthread.h>

using namespace std;

ESP8266* ESP8266::getInstance() {
	static ESP8266 obj;
	return &obj;
}

ESP8266::ESP8266() {
	start();
}

ESP8266::~ESP8266() {
	close();
}

EventHandler::~EventHandler() {}

Event::~Event() {}

void ESP8266::setHttpHandler(HttpHandler *httphandler) {
	this->httphandler = httphandler;
}

HttpHandler* ESP8266::getHttpHandler() {
	return this->httphandler;
}

void ESP8266::close() {
	this->connected_to_serial = 0;
	this->serial.Close();
}

void ESP8266::start() {
	syslog(LOG_INFO, "%s", "Setting up the esp8266 wifi module.");
	if (this->serial.Open(DEVICE, 115200) != 1) {
		syslog(LOG_ERR, "Error opening serial port %s", DEVICE);
	} else {
		this->connected_to_serial = 1;
		this->loadConfig();
	}
}

void ESP8266::restart() {
	close();
	start();
}

void ESP8266::handleEvent(Event *event) {
	SteerEvent *steerEvent = dynamic_cast<SteerEvent *>(event);
	if(steerEvent != NULL) {
		printf("SteerEvent: %i\n", steerEvent->getAngle());
		this->serial.WriteString("<event>angle=30</event>");
	}

	EngineEvent *engineEvent = dynamic_cast<EngineEvent *>(event);
	if(engineEvent != NULL) {
		printf("EngineEvent: %i\n", engineEvent->getThrottle());
		this->serial.WriteString("<event>throttle=10</event>");
	}

}

int ESP8266::isConnectedToSerial() {
	return this->connected_to_serial;
}

int ESP8266::isConnectedToESP8266() {
	std::string alive = this->callFunction("ping()", 2000);
	size_t found = alive.find("pong");
	if (found != string::npos) {
		return 1;
	}
	return 0;
}

void ESP8266::setConfig(ESPConfig *config) {
	json_t *root = json_object();
	json_object_set_new( root, "wifi_mode", json_integer(config->wifi_mode));

	json_t *internet = json_object();
	json_object_set_new( root, "internet", internet);
	json_object_set_new( internet, "ssid", json_string(config->internet_ssid.c_str()));
	json_object_set_new( internet, "pwd", json_string(config->internet_pwd.c_str()));
	json_object_set_new( internet, "connected", json_string(config->internet_connected == 0 ? "false" : "true"));

	json_t *ap = json_object();
	json_object_set_new(root, "ap", ap);
	json_object_set_new(ap, "ssid", json_string(config->ap_ssid.c_str()));
	json_object_set_new(ap, "pwd", json_string(config->ap_pwd.c_str()));
	json_object_set_new(ap, "dhcp", json_string(config->ap_dhcp.c_str()));
	json_object_set_new(ap, "netmask", json_string(config->ap_netmask.c_str()));
	json_object_set_new(ap, "gateway", json_string(config->ap_gateway.c_str()));
	json_object_set_new(ap, "connected", json_string(config->ap_connected == 0 ? "false" : "true"));

	this->serial.WriteString(json_dumps(root, 0));
}

ESPConfig ESP8266::getConfig() {
	return this->config;
}

int ESP8266::loadConfig() {
	std::string raw = this->callFunction("getconfig()", 2000);
	std::string configJson = raw.substr(0, raw.length() -2);

//	printf("Config: %s.\n", configJson.c_str());
	json_t* root = parseRoot(configJson.c_str(), configJson.length());
	if (root == NULL) {
		return 0;
	}
	ESPConfig config;
	json_t *wifi_mode_value = json_object_get(root, "wifi_mode");
	config.wifi_mode = json_number_value(wifi_mode_value);

	json_t *internet_value = json_object_get(root, "internet");
	json_t *internet_ssid_value = json_object_get(internet_value, "ssid");
	json_t *internet_pwd_value = json_object_get(internet_value, "pwd");
	json_t *internet_connected_value = json_object_get(internet_value, "connected");
	config.internet_ssid = getJsonString(internet_ssid_value);
	config.internet_pwd = getJsonString(internet_pwd_value);
	config.internet_connected = getJsonString(internet_connected_value).compare("false") ? 0 : 1;

	json_decref(internet_ssid_value);
	json_decref(internet_pwd_value);

	json_t *ap_value = json_object_get(root, "ap");
	json_t *ap_ssid_value = json_object_get(ap_value, "ssid");
	json_t *ap_pwd_value = json_object_get(ap_value, "pwd");
	json_t *ap_dhcp_value = json_object_get(ap_value, "dhcp");
	json_t *ap_netmask_value = json_object_get(ap_value, "netmask");
	json_t *ap_gateway_value = json_object_get(ap_value, "gateway");
	json_t *ap_connected_value = json_object_get(ap_value, "connected");

	config.ap_ssid = getJsonString(ap_ssid_value);
	config.ap_pwd = getJsonString(ap_pwd_value);
	config.ap_dhcp = getJsonString(ap_dhcp_value);
	config.ap_netmask = getJsonString(ap_netmask_value);
	config.ap_gateway = getJsonString(ap_gateway_value);
	config.ap_connected = getJsonString(ap_connected_value).compare("false") ? 0 : 1;

	json_decref(ap_value);
	json_decref(ap_ssid_value);
	json_decref(ap_pwd_value);
	json_decref(ap_dhcp_value);
	json_decref(ap_netmask_value);
	json_decref(ap_gateway_value);
	json_decref(ap_connected_value);

	this->config = config;
	return 1;
}

string ESP8266::callFunction(string func, int delay) {
	string funcCall = func + "\n";
//	printf("Call func request: %s.\n", func.c_str());
	string result;
	if(this->serial.WriteString(funcCall.c_str()) != 1) {
		syslog(LOG_ERR, "Error calling function %s from wifi module.", funcCall.c_str());
	} else {
		char buffer[4096];
		this->serial.ReadString(buffer, '\0', 4096, delay);
		result = buffer;
//		printf("Call func response: %s.", result.c_str());
//		if (result.find("stdin:1:") != string::npos) {
//			this->callFunction("reset()");
//		}
		size_t found = result.find("http_request:");
		if (found != string::npos) {
			printf("Another request %i %s", found, result.c_str());
//			string next = result.substr(found);
//			char *buf = new char[next.length() + 1];
//			strcpy(buf, next.c_str());
//			printf("Next request, %i, %s\n", found, result.substr(found).c_str());
//			handleRequest(buf, this);
//			delete [] buf;
		}
	}
	return result;
}

Request buildRequest(char *req, ESP8266 *esp8266) {
	Request request;
	request.method = strtok(req, " ");
	request.path = strtok(NULL, " ");
	request.version = strtok(NULL, " ");
	int more = 1;
	char buffer2[4096];
	// Negeer de rest van de request header voor nu. (Zouden we op de esp kunnen weghalen)
	while (more == 1 && esp8266->serial.ReadString(buffer2, '\n', 4096, 0) > 0) {
		if (strlen(buffer2) == 2) {
			more = 0;
		}
	}
	return request;
}

void resolveAndSend(char* req, ESP8266 *esp8266) {
	printf("REQUEST: %s", req);
	Request request = buildRequest(req, esp8266);
	Response response = esp8266->getHttpHandler()->resolveRequest(&request);
	for(size_t i=0;i<response.header.size();i++) {
		esp8266->callFunction("http_response(\"" + response.header.at(i) + "\")");
	}
	for(size_t i=0;i<response.body.size();i++) {
		esp8266->callFunction("http_response(\"" + response.body.at(i) + "\")");
	}
	string next = esp8266->callFunction("http_response(\"RESPONSE_END\")");
	printf("NEXT: %s", next.c_str());
}

void *listenForSerialMessages(void *params) {
	ESP8266 *esp8266 = (ESP8266 *) params;
	while(esp8266->isConnectedToSerial() && esp8266->isConnectedToESP8266()) {
		syslog(LOG_INFO, "%s", "Listing for serial messages...");
		char buffer[4096];
		if (esp8266->serial.ReadString(buffer, '\n', 4096, 0) > 0) {
			string request = buffer;
			size_t found = request.find("http_request:");
			if (found != string::npos) {
				char *header = &buffer[13];
				resolveAndSend(header, esp8266);
			}
		}
	}
	printf("Stopped listing for serial messages.\n");
	pthread_exit(NULL);
}

void ESP8266::startSerialListenThread() {
	printf("Start serial.\n");
	if (this->isConnectedToESP8266()) {
		pthread_t http_thread;
		if (pthread_create(&http_thread, NULL, listenForSerialMessages, this)) {
			perror("Can't create serial listen thread");
		}
	} else {
		syslog(LOG_ERR, "Error opening connection to ESP8266.");
	}
}

//void *httpThread(void *params) {
//	ESP8266 *esp8266 = (ESP8266 *) params;
//	syslog(LOG_INFO, "%s", "Setting up the http thread.");
//	while (esp8266->isConnectedToSerial() && esp8266->isConnectedToESP8266()) {
//		syslog(LOG_INFO, "%s", "Listing for http requests...");
//		char buffer[4096];
//		if (esp8266->serial.ReadString(buffer, '\n', 4096, 0) > 0) {
//			printf("Buffer: %s\n", buffer);
//			char *header = &buffer[13];
//			printf("DATA: %s\n", header);
//			Request request;
//			request.method = strtok(header, " ");
//			request.path = strtok(NULL, " ");
//			request.version = strtok(NULL, " ");
//			int more = 1;
//			char buffer2[4096];
//			// The rest of the header, we don't care for now.
//			while (more == 1 && esp8266->serial.ReadString(buffer2, '\n', 4096, 0) > 0) {
//				if (strlen(buffer2) == 2) {
//					more = 0;
//				}
//			}
////			esp8266->handleRequest(&request);
//		}
//	}
//	pthread_exit(NULL);
//}
