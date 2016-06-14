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
static int dutyMax = 1810000;
static int dutyMiddle = 1550000;
static int dutyMin = 1150000;

#define STEP 50000

Steer::Steer() {
	int period = 20000000;
	int pwmNr = 1;
	pwm = PWM(pwmNr);
	if (pwm.setPeriod(period) > 0) {
		syslog(LOG_ERR, "Could not set pwm period: %s", pwm.getName());
	}
	if (pwm.setDuty(dutyMiddle) > 0) {
		syslog(LOG_ERR, "Could not set pwm duty: %s", pwm.getName());
	}
	if (pwm.setPolarity(0) > 0) {
		syslog(LOG_ERR, "Could not set pwm polarity: %s", pwm.getName());
	}
	if (pwm.start() > 0) {
		syslog(LOG_ERR, "Could not start pwm port: %s", pwm.getName());
	}
	angle = dutyMiddle;
	syslog(LOG_INFO, "%s", "Setting up steering wheel.");

	syslog(LOG_INFO, "%s", "Here is your steering wheel.");
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

void Steer::left() {
	if (angle >= dutyMin) {
		setAngle(angle - STEP);
	}
}

void Steer::right() {
	if (angle <= dutyMax) {
		setAngle(angle + STEP);
	}
}
