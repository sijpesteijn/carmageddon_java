/*
 * Steer.h
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_STEER_H_
#define OBJECTS_STEER_H_

#include <syslog.h>

class Steer
{
	public:
		static Steer* getInstance();
		void setAngle(int a);
		int getAngle();
	private:
		int angle;
		Steer();
};


Steer::Steer() {
	angle = 0;
	syslog(LOG_INFO, "%s", "Setting up steering wheel.");

	syslog(LOG_INFO, "%s", "Here is your steering wheel.");
}

Steer* Steer::getInstance() {
	static Steer obj;
	return &obj;
}

void Steer::setAngle(int a) {
	angle = a;
}

int Steer::getAngle() {
	return angle;
}

#endif /* OBJECTS_STEER_H_ */
