/*
 * Steer.h
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_ENGINE_H_
#define OBJECTS_ENGINE_H_

#include "EventHandler.h"

class EngineEvent: public Event {
public:
	EngineEvent(int throttle);
	~EngineEvent();
	int getThrottle();
private:
	int throttle;
};

class Engine {
public:
	static Engine* getInstance(EventHandler *eventHandler);
	void setThrottle(int t);
	int getThrottle();
	void slowDown();
	void speedUp();
private:
	int throttle;
	EventHandler *eventHandler;
	Engine(EventHandler *eventHandler);
};

#endif /* OBJECTS_ENGINE_H_ */
