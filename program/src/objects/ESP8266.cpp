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
	this->uart = new UART(uart4, B115200);
	syslog(LOG_INFO, "%s", "uart started");
}

int ESP8266::isConnected() {
	return 1;
}

bool meetFilter(const string line) {
	return 0;
}

Versions* ESP8266::getStatus() {
	Versions *versions = new Versions();
	list<string> lines = this->sendMessage("status()\n");
	for (std::list<string>::iterator it=lines.begin(); it!=lines.end(); ++it) {
		string line = *it;
		if (line.find("AT") == 0) {
			versions->board = line;

		}
		if (line.find("SDK") == 0) {
			versions->sdk = line;
		}
	}
	return versions;
}

list<string> ESP8266::getAccessPoints() {
	list<string> accesspoints;
	list<string> lines = this->sendMessage("AT+CWLAP");
	for (std::list<string>::iterator it=lines.begin(); it!=lines.end(); ++it) {
		string line = *it;
//		printf("%s.\n", line.c_str());
	}
	return accesspoints;
}

list<string> ESP8266::sendMessage(string cmd) {
	list<string> names;
	this->uart->sendData(cmd.append("\r\n"));
	while(1) {
	string msg = this->uart->readData();
	syslog(LOG_INFO, "m:%i %s.\n", msg.length(), msg.c_str());
	int start = 0, end;
	while ((end = msg.find('\n', start)) > 0) {
		if (end - start > 1) {
			string line = msg.substr(start, end - 1);
			int cr = line.find_first_of('\n');
			if (cr > 0) {
				line = line.substr(0, cr);
//				syslog(LOG_INFO, "#:%i f:%i s:%s.\n", line.length(), cr, line.c_str());
				names.push_back(line);
	//			printf("%s\n", line.c_str());
	//			syslog(LOG_INFO, "%d %d\n", start, end);
			}
		}
//		printf("s: %i e: %i\n", start, end);
		start = end + 1;
	}
	}
	return names;
}
