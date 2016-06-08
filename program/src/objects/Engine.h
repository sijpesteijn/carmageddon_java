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
	int throttle = 0;
	static Engine* getInstance() {
		static Engine obj;
		return &obj;
	}
private:
	Engine() {
		syslog(LOG_INFO, "%s", "Setting up the engine.");

		syslog(LOG_INFO, "%s", "Here is the engine.");
	};
};

#endif /* OBJECTS_ENGINE_H_ */
