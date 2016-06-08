/*
 * Steer.h
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_STEER_H_
#define OBJECTS_STEER_H_

#include <iostream>
using namespace std;

class Steer {
public:
	int angle = 0;
	static Steer* getInstance() {
		static Steer obj;
		return &obj;
	}
private:
	Steer() {
		syslog(LOG_INFO, "%s", "Setting up steering wheel.");

		syslog(LOG_INFO, "%s", "Here is your steering wheel.");
	};
};

#endif /* OBJECTS_STEER_H_ */
