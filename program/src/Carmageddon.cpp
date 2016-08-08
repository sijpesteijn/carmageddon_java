//============================================================================
// Name        : carmageddon.cpp
// Author      : Carmageddon
// Version     :
// Copyright   : Nothing much
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <syslog.h>
#include <stdio.h>
#include <unistd.h>
#include <termios.h>
#include <cstdlib>
#include <pthread.h>
#include "objects/ESP8266.h"
#include "./objects/Steer.h"
#include "./objects/Engine.h"
#include "./objects/Camera.h"

using namespace std;

void *wifiThread(void *params) {
	ESP8266 *esp8266 = ESP8266::getInstance(); // We hebben maar een wifi module.
	if (esp8266->isConnectedToSerial() == 1 && esp8266->isConnectedToESP8266() == 1) {
		ESPConfig config = esp8266->getConfig();
		printf("B %s.\n", config.internet_ssid.c_str());
	}
	pthread_exit(NULL);
}

int main() {
	// Set up syslog. (tail -f /var/log/syslog | grep carmageddon)
	openlog("carmageddon-bbb", LOG_PID | LOG_CONS | LOG_NDELAY | LOG_NOWAIT, LOG_LOCAL0);
	setlogmask(LOG_UPTO(LOG_DEBUG));
	syslog(LOG_INFO, "%s", "Starting Carmaggedon...");

	pthread_t wifi_thread;
	if (pthread_create(&wifi_thread, NULL, wifiThread, NULL))
		perror("Can't create message_handler thread");

//	Camera *camera = Camera::getInstance(); // We hebben maar een camera.
//	syslog(LOG_INFO, "Camera connected: %i", camera->isConnected());

	Engine *engine = Engine::getInstance(); // We hebben maar een motor.
	syslog(LOG_INFO, "Engine throttle: %i", engine->getThrottle());

	Steer *steer = Steer::getInstance(); // We hebben maar een stuur.
	syslog(LOG_INFO, "Steering wheel angle: %i", steer->getAngle());

//	CPU cpu(camera, steer, engine);

	struct termios old_tio, new_tio;
	    unsigned char c;

	    /* get the terminal settings for stdin */
	    tcgetattr(STDIN_FILENO,&old_tio);

	    /* we want to keep the old setting to restore them a the end */
	    new_tio=old_tio;

	    /* disable canonical mode (buffered i/o) and local echo */
	    new_tio.c_lflag &=(~ICANON & ~ECHO);

	    /* set the new settings immediately */
	    tcsetattr(STDIN_FILENO,TCSANOW,&new_tio);

	    do {
	         c=getchar();
	         if (c == 65) {
	        	 engine->speedUp();
	        	 printf("Up Arrow %i\n", engine->getThrottle());
	         }
	         if (c == 66) {
	        	 engine->slowDown();
	        	 printf("Down Arrow %i\n", engine->getThrottle());
	         }
	         if (c == 67) {
	        	 steer->right();
	        	 printf("Right Arrow %i\n", steer->getAngle());
	         }
	         if (c == 68) {
	        	 steer->left();
	        	 printf("Left Arrow %i\n", steer->getAngle());
	         }
	    } while(c!='q');

	    /* restore the former settings */
	    tcsetattr(STDIN_FILENO,TCSANOW,&old_tio);


	syslog(LOG_INFO, "%s", "Carmaggedon stopped");
	return 0;
}