/*
 * ESP8266.h
 *
 *  Created on: Jul 3, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_ESP8266_H_
#define OBJECTS_ESP8266_H_

#include "Uart.h"
#include <list>

using namespace std;

typedef struct {
	std::string board;
	std::string sdk;
} Versions;

class ESP8266 {
public:
	static ESP8266* getInstance();
	Versions* getVersions();
	list<string> getAccessPoints();
	int isConnected();
private:
	ESP8266();
	UART *uart;
	list<string> sendMessage(std::string cmd);

};



#endif /* OBJECTS_ESP8266_H_ */
