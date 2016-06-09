/*
 * Steer.h
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_ENGINE_H_
#define OBJECTS_ENGINE_H_

#include <syslog.h>
#include <iostream>
using namespace std;

class Engine {
public:
	static Engine* getInstance();
	void setThrottle(int t);
	int getThrottle();
private:
	int throttle;
	Engine();
};

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

#endif /* OBJECTS_ENGINE_H_ */
