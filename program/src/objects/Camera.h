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
	static Camera* getInstance();
	int isConnected();
private:
	int connected;
	Camera();
};

Camera::Camera() {
	connected = 0;
	syslog(LOG_INFO, "%s", "Setting up camera.");

	syslog(LOG_INFO, "%s", "Here is the camera.");
}

int Camera::isConnected() {
	return connected;
}

Camera* Camera::getInstance() {
	static Camera camera;
	return &camera;
}

#endif /* OBJECTS_CAMERA_H_ */
