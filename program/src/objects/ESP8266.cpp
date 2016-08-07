/*
 * ESP8266.cpp
 *
 *  Created on: Jul 3, 2016
 *      Author: gijs
 */

#include "ESP8266.h"
#include "../jansson/jansson.h"
#include "../Utils.h"
#include <syslog.h>
#include <stdlib.h>
#include <iostream>
#include <algorithm>
#include <string>
using namespace std;

ESP8266* ESP8266::getInstance() {
	static ESP8266 obj;
	return &obj;
}

ESP8266::ESP8266() {
	syslog(LOG_INFO, "%s", "Setting up the esp8266 wifi module.");
	if (this->serial.Open(DEVICE, 115200) != 1) {
		syslog(LOG_ERR, "Error opening serial port %s", DEVICE);
	} else {
		this->connected_to_serial = 1;
		std::string alive = this->callFunction("alive()");
		if (alive.compare("1") == 0) {
			this->connected_to_esp8266 = 1;
		} else {
			syslog(LOG_ERR, "Error opening connection to ESP8266.");
		}
	}
}

int ESP8266::isConnectedToSerial() {
	return this->connected_to_serial;
}

int ESP8266::isConnectedToESP8266() {
	return this->connected_to_esp8266;
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
	std::string status = this->callFunction("statas()");
	printf("Status: %s\n", status.c_str());
	ESPConfig config;

	json_t* root = parseRoot(status.c_str(), status.length());
	if (root == NULL) {
		this->connected_to_esp8266 = 0;
	}
	json_t *wifi_mode_value = json_object_get(root, "wifi_mode");
	config.wifi_mode = json_number_value(wifi_mode_value);

	json_t *internet_value = json_object_get(root, "internet");
	json_t *internet_ssid_value = json_object_get(internet_value, "ssid");
	json_t *internet_pwd_value = json_object_get(internet_value, "pwd");
	json_t *internet_connected_value = json_object_get(internet_value, "connected");
	config.internet_ssid = getString(internet_ssid_value);
	config.internet_pwd = getString(internet_pwd_value);
//	config.internet_connected = getString(internet_connected_value).compare("false") ? 0 : 1;

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
//	config.ap_connected = getString(ap_connected_value).compare("false") ? 0 : 1;

	json_decref(ap_value);
	json_decref(ap_ssid_value);
	json_decref(ap_pwd_value);
	json_decref(ap_dhcp_value);
	json_decref(ap_netmask_value);
	json_decref(ap_gateway_value);
	json_decref(ap_connected_value);

	this->connected_to_esp8266 = 1;
	return config;
}

std::string ESP8266::callFunction(std::string func) {
	std::string funcCall = func + "\n";
	std::string result;
	if(this->serial.WriteString(funcCall.c_str()) != 1) {
		syslog(LOG_ERR, "Error calling function %s from wifi module.", funcCall.c_str());
	} else {
		char buffer[4096];
		this->serial.ReadString(buffer, '\0', 4096, 2000);
		result = buffer;
	}
	int start = result.find_first_of("\n");
	return result.substr(start+1, (result.length() - start) - 4);
}
