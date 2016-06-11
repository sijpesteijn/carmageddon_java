/*
 * Steer.cpp
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */
#include <syslog.h>
#include "Steer.h"
#include "Pwm.h"

static PWM pwm;

Steer::Steer() {
	int dutyMax = 2000000;
	int dutyMin = 880000;
	int pwmNr = 0;
	pwm = PWM(pwmNr, dutyMax);
	if (pwm.setDuty(dutyMin) > 0) {
		syslog(LOG_ERR, "Could not set pwm duty: %s", pwm.getName());
	}
	if (pwm.setPolarity(1) > 0) {
		syslog(LOG_ERR, "Could not set pwm polarity: %s", pwm.getName());
	}
	if (pwm.start() > 0) {
		syslog(LOG_ERR, "Could not start pwm port: %s", pwm.getName());
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
	if (pwm.setDuty(a) > 0) {
		syslog(LOG_INFO,"%s", "Could not set steering wheel angle.");
	} else {
		syslog(LOG_DEBUG, "Steering wheel angle set to: %i.", angle);
		angle = a;
	}
}

int Steer::getAngle() {
	return angle;
}
