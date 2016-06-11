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

Engine::Engine() {
	syslog(LOG_INFO, "%s", "Setting up the engine.");
	int pwmNr = 0;
	int maxDuty= 900000;
	int minDuty= 800000;
	pwm = PWM(pwmNr, 1000000);
	if (pwm.setDuty(minDuty) > 0) {
		syslog(LOG_ERR, "Could not set pwm duty: %s", pwm.getName());
	}
	if (pwm.setPolarity(1) > 0) {
		syslog(LOG_ERR, "Could not set pwm polarity: %s", pwm.getName());
	}
	if (pwm.start() > 0) {
		syslog(LOG_ERR, "Could not start pwm port: %i", pwmNr);
	}
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
