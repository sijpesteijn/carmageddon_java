/*
 * Steer.h
 *
 *  Created on: Jun 8, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_CAMERA_H_
#define OBJECTS_CAMERA_H_

class Camera {
public:
	static Camera* getInstance();
	int isConnected();
private:
	int connected;
	Camera();
};

#endif /* OBJECTS_CAMERA_H_ */
