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
#include "HttpHandler.h"

using namespace std;

class ESPConfig {
public:
	int wifi_mode;
	string internet_ssid;
	string internet_pwd;
	int internet_connected;

	string ap_ssid;
	string ap_pwd;
	string ap_dhcp;
	string ap_netmask;
	string ap_gateway;
	int ap_connected;
};

#define DEVICE "/dev/ttyO4"

class ESP8266: public EventHandler {
public:
	static ESP8266* getInstance();
	~ESP8266();
	int isConnectedToSerial();
	int isConnectedToESP8266();
	Serialib serial;
	ESPConfig getConfig();
	void setConfig(ESPConfig* config);
	void handleEvent(Event *event);
	void setHttpHandler(HttpHandler *httphandler);
	HttpHandler *getHttpHandler();
	string callFunction(string func, int delay = 5);
	void restart();
	void startSerialListenThread();
private:
	int connected_to_serial = 0;
	ESPConfig config;
	HttpHandler *httphandler;
	ESP8266();
	void close();
	void start();
	int loadConfig();
};

#endif /* OBJECTS_ESP8266_H_ */
