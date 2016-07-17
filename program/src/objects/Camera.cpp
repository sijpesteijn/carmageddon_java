/*
 * Camera.c
 *
 *  Created on: Jun 9, 2016
 *      Author: gijs
 */

#include "Camera.h"
#include <syslog.h>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

Camera::Camera() {

	VideoCapture cap(0);
	if (!cap.isOpened()) {
		cout << "Cannot open the video cam" << endl;
	}
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

