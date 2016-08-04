/*
 * Pwm.h
 *
 *  Created on: Jun 9, 2016
 *      Author: gijs
 */

#include <unistd.h>
#include <syslog.h>
#include <stdlib.h>
#include <string>
#include <sstream>

#include <stdio.h>
#include "Pwm.h"
#include "DeviceTree.h"

pwm pwms[8] = {
		{0,"bone_pwm_P9_22", "pwm_test_P9_22.15"},
		{1,"bone_pwm_P9_42", "pwm_test_P9_42.16"},
		{2,"bone_pwm_P9_22", "pwm_test_P9_22.15"},
		{3,"bone_pwm_P9_22", "pwm_test_P9_22.15"},
		{4,"bone_pwm_P9_22", "pwm_test_P9_22.15"},
		{5,"bone_pwm_P9_22", "pwm_test_P9_22.15"},
		{6,"bone_pwm_P9_22", "pwm_test_P9_22.15"},
		{7,"bone_pwm_P9_22", "pwm_test_P9_22.15"}
};

PWM::PWM(int pwm_number) {
	pwmNr = pwm_number;
	polarity = 0;
	duty = 0;
	run = 0;
	period = 0;
	struct overlay* ol = (struct overlay*) malloc(sizeof(struct overlay));
	ol->file_name = pwms[pwmNr].bone_name;
	ol->board_name = "Override Board Name";
	ol->manufacturer = "Override Manuf";
	ol->part_number = pwms[pwmNr].bone_name;
	ol->version = "00A0";

	DeviceTreeOverlay deviceTree;
	deviceTree.load_device_tree_overlay(ol);
	ol->file_name = "am33xx_pwm";
	ol->part_number = "am33xx_pwm";
	deviceTree.load_device_tree_overlay(ol);
}

int PWM::setPolarity(int p) {
	char command[255];
	sprintf(command, "echo %d > %s/%s/polarity", p, PWM_PATH, pwms[pwmNr].pwm_name);
	if (system(command) > 0) {
		return 1;
	}
	polarity = p;
	return 0;
}

int PWM::getPolarity() {
	return polarity;
}

int PWM::setPeriod(int p) {
	char command[255];
	sprintf(command, "echo %d > %s/%s/period", p, PWM_PATH, pwms[pwmNr].pwm_name);
	if (system(command) > 0) {
		return 1;
	}
	period = p;
	return 0;
}

int PWM::getPeriod() {
	return period;
}

int PWM::setDuty(int d) {
	char command[255];
	sprintf(command, "echo %d > %s/%s/duty", d, PWM_PATH, pwms[pwmNr].pwm_name);
	if (system(command) > 0) {
		return 1;
	}
	duty = d;
	return 0;
}

int PWM::getDuty() {
	return duty;
}

int PWM::toggle(int r) {
	char command[255];
	sprintf(command, "echo %d > %s/%s/run", r, PWM_PATH, pwms[pwmNr].pwm_name);
	if (system(command) > 0) {
		return 1;
	}
	run = r;
	return 0;
}

int PWM::start() {
	return toggle(1);
}

int PWM::stop() {
	return toggle(0);
}

int PWM::isRunning() {
	return run;
}

const char* PWM::getName() {
	return pwms[pwmNr].pwm_name;
}
