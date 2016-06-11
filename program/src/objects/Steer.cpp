/*
 * Steer.cpp
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */
#include <syslog.h>
#include "Steer.h"
#include "Pwm.h"

PWM pwm;

Steer::Steer() {
	int pwmNr = 0;
	pwm = PWM(pwmNr);
	if (pwm.open() > 0) {
		syslog(LOG_ERR, "Could not open pwm port: %i", pwmNr);
	}
	angle = 0;
	syslog(LOG_INFO, "%s", "Setting up steering wheel.");

	syslog(LOG_INFO, "%s", "Here is your steering wheel.");
}

Steer::~Steer() {

}

Steer* Steer::getInstance() {
	static Steer obj;
	return &obj;
}

void Steer::setAngle(int a) {
	pwm.setDuty(a);
	angle = a;
}

int Steer::getAngle() {
	return angle;
}
