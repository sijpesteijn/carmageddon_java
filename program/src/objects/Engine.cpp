/*
 * Steer.h
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */

#include "Engine.h"
#include "Pwm.h"
#include <syslog.h>

static PWM pwm;
static int dutyMax= 2000000;
static int dutyMiddle = 1200000;
static int dutyMin= 800000;


#define STEP 50000

Engine::Engine() {
	syslog(LOG_INFO, "%s", "Setting up the engine.");
	int pwmNr = 0;
	int period = 20000000;
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
		syslog(LOG_ERR, "Could not start pwm port: %i", pwmNr);
	}
	throttle = dutyMiddle;
	syslog(LOG_INFO, "%s", "Here is the engine.");
}

Engine* Engine::getInstance() {
	static Engine obj;
	return &obj;
}

void Engine::setThrottle(int t) {
	if (pwm.setDuty(t) > 0) {
		syslog(LOG_INFO,"%s","Could not set throttle.");
	} else {
		syslog(LOG_DEBUG,"Throttle set to: %i", throttle);
		throttle = t;
	}
}

int Engine::getThrottle() {
	return throttle;
}

void Engine::speedUp() {
	if (throttle + STEP <= dutyMax)
		setThrottle(throttle + STEP);
}

void Engine::slowDown() {
	if (throttle - STEP >= dutyMin)
		setThrottle(throttle - STEP);
}
