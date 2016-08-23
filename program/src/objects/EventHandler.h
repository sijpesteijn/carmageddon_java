/*
 * EventHandler.h
 *
 *  Created on: Aug 17, 2016
 *      Author: carmageddon
 */

#ifndef OBJECTS_EVENTHANDLER_H_
#define OBJECTS_EVENTHANDLER_H_

class Event {
public:
	virtual ~Event();
};

class EventHandler {
public:
	virtual void handleEvent(Event *event) =0;
	virtual ~EventHandler();
};

#endif /* OBJECTS_EVENTHANDLER_H_ */
