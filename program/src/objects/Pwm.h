/*
 * Pwm.h
 *
 *  Created on: Jun 9, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_PWM_H_
#define OBJECTS_PWM_H_

#include <unistd.h>
#include <map>
#define PWM_PATH "/sys/devices/ocp.3/"

struct pwm {
	int id;
	const char *bone_name;
	const char *pwm_name;
};

class PWM {
public:
	PWM(int pwm_number = 0, int period = 20000000);
	int setPolarity(int p);
	int getPolarity();
	int setDuty(int d);
	int getDuty();
	int setPeriod(int p);
	int getPeriod();
	int start();
	int stop();
	int isRunning();
	const char *getName();
private:
	int pwmNr;
	int period;
	int polarity;
	int duty;
	int run;
	int toggle(int r);
};

#endif /* OBJECTS_PWM_H_ */
