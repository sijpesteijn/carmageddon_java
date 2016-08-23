/*
 * ESP8266.h
 *
 *  Created on: Jul 3, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_ESP8266_H_
#define OBJECTS_ESP8266_H_

#include "Serialib.h"
#include "EventHandler.h"
#include <string>

class ESPConfig {
public:
	int wifi_mode;
	std::string internet_ssid;
	std::string internet_pwd;
	int internet_connected;

	std::string ap_ssid;
	std::string ap_pwd;
	std::string ap_dhcp;
	std::string ap_netmask;
	std::string ap_gateway;
	int ap_connected;
};

#define DEVICE "/dev/ttyO4"

class ESP8266: public EventHandler {
public:
	static ESP8266* getInstance(std::string html_root);
	~ESP8266();
	int isConnectedToSerial();
	int isConnectedToESP8266();
	void startHttpThread();
	Serialib serial;
	ESPConfig getConfig();
	void setConfig(ESPConfig* config);
	void handleEvent(Event *event);
	std::string getHtmlRoot();
private:
	int connected_to_serial = 0;
	std::string html_root;
	ESP8266(std::string html_root);
	std::string callFunction(std::string func);
};

#endif /* OBJECTS_ESP8266_H_ */
