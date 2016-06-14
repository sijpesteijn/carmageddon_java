/*
 * Steer.h
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_ENGINE_H_
#define OBJECTS_ENGINE_H_

class Engine {
public:
	static Engine* getInstance();
	void setThrottle(int t);
	int getThrottle();
	void slowDown();
	void speedUp();
private:
	int throttle = 0;
	Engine();
};

#endif /* OBJECTS_ENGINE_H_ */
