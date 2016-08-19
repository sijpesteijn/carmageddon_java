/*
 * Steer.h
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_STEER_H_
#define OBJECTS_STEER_H_

#include "EventHandler.h"


class SteerEvent: public Event {
public:
	SteerEvent(int angle);
	~SteerEvent();
	int getAngle();
private:
	int angle;
};

class Steer
{
	public:
		static Steer* getInstance(EventHandler *eventHandler);
		void setAngle(int a);
		int getAngle();
		void left();
		void right();
	private:
		int angle;
		EventHandler *eventHandler;
		Steer(EventHandler *eventHandler);
};

#endif /* OBJECTS_STEER_H_ */
