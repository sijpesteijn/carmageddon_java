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
#define PWM_PATH "/sys/class/pwm/"

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
private:
	int pwmNr;
	int period;
	int polarity;
	int duty;
};

#endif /* OBJECTS_PWM_H_ */
