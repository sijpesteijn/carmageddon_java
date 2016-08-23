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
#include <syslog.h>
#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <algorithm>
#include <string>
#include <pthread.h>

using namespace std;

void *httpThread(void *params) {
	ESP8266 *esp8266 = (ESP8266 *) params;
	syslog(LOG_INFO, "%s", "Setting up the http thread.");
	while (esp8266->isConnectedToSerial() && esp8266->isConnectedToESP8266()) {
//		esp8266->getConfig();
		syslog(LOG_INFO, "%s", "Listing for http requests...");
		char buffer[4096];
		if (esp8266->serial.ReadString(buffer, '\n', 4096, 0) > 0) {
			printf("DATA: %s\n", buffer);
			int read = 1;
			char buffer2[4096];
			while (read == 1 && esp8266->serial.ReadString(buffer2, '\n', 4096, 0) > 0) {
				printf("DATA2: %s, %i\n", buffer2, strlen(buffer2));
				if (strlen(buffer2) == 2) {
					read = 0;
				}
			}
			ifstream infile;
			string filename;
			filename = esp8266->getHtmlRoot() + "index.html";
			printf("Filename: %s\n", filename.c_str());
			infile.open(filename.c_str());
			esp8266->serial.WriteString("http_response('Eline heeft een nieuwe fiets')");
//			esp8266->serial.WriteString("%3Chttp_response%3Emoemoe%3C%2Fhttp_response%3E");
			esp8266->serial.FlushReceiver();
		}
//		usleep(10);
	}
	pthread_exit(NULL);
}


ESP8266* ESP8266::getInstance(std::string html_root) {
	static ESP8266 obj(html_root);
	return &obj;
}

ESP8266::ESP8266(std::string html_root) {
	syslog(LOG_INFO, "%s", "Setting up the esp8266 wifi module.");
	this->html_root = html_root;
	if (this->serial.Open(DEVICE, 115200) != 1) {
		syslog(LOG_ERR, "Error opening serial port %s", DEVICE);
	} else {
		this->connected_to_serial = 1;
	}
}

ESP8266::~ESP8266() {

}

EventHandler::~EventHandler() {

}

Event::~Event() {

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
	std::string alive = this->callFunction("ping()");
	if (alive.compare(0, 1, "1") == 0) {
		return 1;
	}
	return 0;
}

void ESP8266::startHttpThread() {
	if (this->isConnectedToESP8266()) {
		pthread_t http_thread;
		if (pthread_create(&http_thread, NULL, httpThread, this)) {
			perror("Can't create http thread");
		}
	} else {
		syslog(LOG_ERR, "Error opening connection to ESP8266.");
	}
}


std::string ESP8266::getHtmlRoot() {
	return this->html_root;
}

std::string getString(json_t *value) {
	std::string s (json_string_value(value), json_string_length(value));
	return s;
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
	std::string status = this->callFunction("getconfig()");
//	printf("Status: %s\n", status.c_str());
	ESPConfig config;

	json_t* root = parseRoot(status.c_str(), status.length());
	json_t *wifi_mode_value = json_object_get(root, "wifi_mode");
	config.wifi_mode = json_number_value(wifi_mode_value);

	json_t *internet_value = json_object_get(root, "internet");
	json_t *internet_ssid_value = json_object_get(internet_value, "ssid");
	json_t *internet_pwd_value = json_object_get(internet_value, "pwd");
	json_t *internet_connected_value = json_object_get(internet_value, "connected");
	config.internet_ssid = getString(internet_ssid_value);
	config.internet_pwd = getString(internet_pwd_value);
	config.internet_connected = getString(internet_connected_value).compare("false") ? 0 : 1;

	json_decref(internet_ssid_value);
	json_decref(internet_pwd_value);

	json_t *ap_value = json_object_get(root, "ap");
	json_t *ap_ssid_value = json_object_get(ap_value, "ssid");
	json_t *ap_pwd_value = json_object_get(ap_value, "pwd");
	json_t *ap_dhcp_value = json_object_get(ap_value, "dhcp");
	json_t *ap_netmask_value = json_object_get(ap_value, "netmask");
	json_t *ap_gateway_value = json_object_get(ap_value, "gateway");
	json_t *ap_connected_value = json_object_get(ap_value, "connected");

	config.ap_ssid = getString(ap_ssid_value);
	config.ap_pwd = getString(ap_pwd_value);
	config.ap_dhcp = getString(ap_dhcp_value);
	config.ap_netmask = getString(ap_netmask_value);
	config.ap_gateway = getString(ap_gateway_value);
	config.ap_connected = getString(ap_connected_value).compare("false") ? 0 : 1;

	json_decref(ap_value);
	json_decref(ap_ssid_value);
	json_decref(ap_pwd_value);
	json_decref(ap_dhcp_value);
	json_decref(ap_netmask_value);
	json_decref(ap_gateway_value);
	json_decref(ap_connected_value);

	return config;
}

std::string ESP8266::callFunction(std::string func) {
	std::string funcCall = func + "\n";
	printf("Call func request: %s.\n", funcCall.c_str());
	std::string result;
	if(this->serial.WriteString(funcCall.c_str()) != 1) {
		syslog(LOG_ERR, "Error calling function %s from wifi module.", funcCall.c_str());
	} else {
		char buffer[4096];
		this->serial.ReadString(buffer, '\0', 4096, 2000);
		result = buffer;
		printf("Call func response: %s.\n", result.c_str());
	}
	int start = result.find_first_of("\n");
	return result.substr(start+1, (result.length() - start) - 5);
}
