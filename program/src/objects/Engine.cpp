/*
 * Steer.h
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */

#include "Engine.h"
#include <syslog.h>

Engine::Engine() {
	throttle = 0;
	syslog(LOG_INFO, "%s", "Setting up the engine.");

	syslog(LOG_INFO, "%s", "Here is the engine.");
}

Engine* Engine::getInstance() {
	static Engine obj;
	return &obj;
}

void Engine::setThrottle(int t) {
	throttle = t;
}

int Engine::getThrottle() {
	return throttle;
}
