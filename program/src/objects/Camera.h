/*
 * Steer.h
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_CAMERA_H_
#define OBJECTS_CAMERA_H_

#include <syslog.h>
#include <iostream>
using namespace std;

class Camera {
public:
	int connected = 0;
	static Camera* getInstance() {
		static Camera obj;
		return &obj;
	}
private:
	Camera() {
		syslog(LOG_INFO, "%s", "Setting up camera.");

		syslog(LOG_INFO, "%s", "Here is the camera.");
	};
};

#endif /* OBJECTS_CAMERA_H_ */
