/*
 * ESP8266.h
 *
 *  Created on: Jul 3, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_ESP8266_H_
#define OBJECTS_ESP8266_H_

#include "Serialib.h"
#include <string>

#define DEVICE "/dev/ttyO4"
class ESP8266 {
public:
	static ESP8266* getInstance();
	void getStatus();
private:
	ESP8266();
	Serialib serial;
	std::string callFunction(std::string func);
};

#endif /* OBJECTS_ESP8266_H_ */
