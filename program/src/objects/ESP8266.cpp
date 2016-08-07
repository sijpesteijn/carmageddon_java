/*
 * ESP8266.cpp
 *
 *  Created on: Jul 3, 2016
 *      Author: gijs
 */

#include "ESP8266.h"
#include "Uart.h"
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
	}
}

void ESP8266::getStatus() {
	std::string status = this->callFunction("status()");
	printf("%s", status.c_str());
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
	return result.substr(result.find_first_of("\n")+1);
}
