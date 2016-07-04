/*
 * ESP8266.h
 *
 *  Created on: Jul 3, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_ESP8266_H_
#define OBJECTS_ESP8266_H_

#include "Uart.h"

class ESP8266 {
public:
	static ESP8266* getInstance();
	int isConnected();
private:
	ESP8266();
	UART *uart;
};



#endif /* OBJECTS_ESP8266_H_ */
