/*
 * Steer.h
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_STEER_H_
#define OBJECTS_STEER_H_

class Steer
{
	public:
		static Steer* getInstance();
		void setAngle(int a);
		int getAngle();
		~Steer();
	private:
		int angle;
		Steer();
};

#endif /* OBJECTS_STEER_H_ */
