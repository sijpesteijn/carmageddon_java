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
static int dutyMiddle = 1400000;
static int dutyMin= 800000;

#define STEP 50000

EngineEvent::EngineEvent(int throttle) {
	this->throttle = throttle;
}

EngineEvent::~EngineEvent() {

}

int EngineEvent::getThrottle() {
	return this->throttle;
}

Engine::Engine(EventHandler *eventHandler) {
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
	this->eventHandler = eventHandler;
	syslog(LOG_INFO, "%s", "Here is the engine.");
}

Engine* Engine::getInstance(EventHandler *eventHandler) {
	static Engine obj(eventHandler);
	return &obj;
}

void Engine::setThrottle(int t) {
	if (pwm.setDuty(t) > 0) {
		syslog(LOG_INFO,"%s","Could not set throttle.");
	} else {
		syslog(LOG_DEBUG,"Throttle set to: %i", throttle);
		throttle = t;
		Event *event = new EngineEvent(throttle);
		this->eventHandler->handleEvent(event);
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
