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

ESP8266* ESP8266::getInstance() {
	static ESP8266 obj;
	return &obj;
}

ESP8266::ESP8266() {
	syslog(LOG_INFO, "%s", "Setting up the esp8266 wifi module.");
	this->uart = new UART(uart4, B115200);
	syslog(LOG_INFO, "%s", "uart started");

//	while(1) {
//		unsigned char receive[100];
//		if (this->uart->readData(receive, 100) < 0) {
//			syslog(LOG_INFO, "%s", "Could not read data from uart port");
//		}
//
//		syslog(LOG_INFO, "Read: %s\n",receive);
//	}
}

int ESP8266::isConnected() {
	return 1;
}
