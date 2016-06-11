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
	int open();
	void setPolarity(int p);
	int getPolarity();
	void setDuty(int d);
	int getDuty();
	void setPeriod(int p);
	int getPeriod();
	void start();
	void stop();
	int isRunning();
private:
	int pwmNr;
	int period;
	int polarity;
	int duty;
	int run;
	void toggle(int r);
};

#endif /* OBJECTS_PWM_H_ */
