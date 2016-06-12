//============================================================================
// Name        : carmageddon.cpp
// Author      : Carmageddon
// Version     :
// Copyright   : Nothing much
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <syslog.h>
#include "./objects/Steer.h"
#include "./objects/Engine.h"
#include "./objects/Camera.h"

using namespace std;

int main() {
	// Set up syslog. (tail -f /var/log/syslog | grep carmageddon)
	openlog("carmageddon-bbb", LOG_PID | LOG_CONS | LOG_NDELAY | LOG_NOWAIT, LOG_LOCAL0);
	setlogmask(LOG_UPTO(LOG_DEBUG));
	syslog(LOG_INFO, "%s", "Starting Carmaggedon...");

	Steer *steer = Steer::getInstance(); // We hebben maar een stuur.
	syslog(LOG_INFO, "Steering wheel angle: %i", steer->getAngle());

	Engine *engine = Engine::getInstance(); // We hebben maar een motor.
	syslog(LOG_INFO, "Engine throttle: %i", engine->getThrottle());

	Camera *camera = Camera::getInstance(); // We hebben maar een camera.
	syslog(LOG_INFO, "Camera connected: %i", camera->isConnected());

	for(int i = 0; i < 100000000;i++) {

	}
	steer->setAngle(1500000);


	syslog(LOG_INFO, "%s", "Carmaggedon stopped");
	return 0;
}
