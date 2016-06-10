/*
 * Camera.c
 *
 *  Created on: Jun 9, 2016
 *      Author: gijs
 */

#include "Camera.h"
#include <syslog.h>

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

