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
		{1,"bone_pwm_P9_22", "pwm_test_P9_22.31"},
		{2,"bone_pwm_P9_22", "pwm_test_P9_22.15"},
		{3,"bone_pwm_P9_22", "pwm_test_P9_22.15"},
		{4,"bone_pwm_P9_22", "pwm_test_P9_22.15"},
		{5,"bone_pwm_P9_22", "pwm_test_P9_22.15"},
		{6,"bone_pwm_P9_22", "pwm_test_P9_22.15"},
		{7,"bone_pwm_P9_22", "pwm_test_P9_22.15"}
};

PWM::PWM(int pwm_number, int p) {
	pwmNr = pwm_number;
	period = p;
	polarity = 0;
	duty = 880000;
	run = 0;
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

int PWM::open() {
	std::string tmp;
	tmp = PWM_PATH + std::string(pwms[pwmNr].pwm_name);

	if (access(tmp.c_str(), F_OK) == -1) {
		syslog(LOG_ERR, "Could not open file: %s", tmp.c_str());
		return 1;
	}
	setPolarity(polarity);
	setPeriod(period);
	setDuty(duty);
	start();
	return 0;
}

void PWM::setPolarity(int p) {
	char command[255];
	sprintf(command, "echo %d > %s/%s/polarity", p, PWM_PATH, pwms[pwmNr].pwm_name);
	system(command);
	polarity = p;
}

int PWM::getPolarity() {
	return polarity;
}

void PWM::setPeriod(int p) {
	char command[255];
	sprintf(command, "echo %d > %s/%s/period", p, PWM_PATH, pwms[pwmNr].pwm_name);
	system(command);
	period = p;
}

int PWM::getPeriod() {
	return period;
}

void PWM::setDuty(int d) {
	char command[255];
	sprintf(command, "echo %d > %s/%s/duty", d, PWM_PATH, pwms[pwmNr].pwm_name);
	system(command);
	duty = d;
}

int PWM::getDuty() {
	return duty;
}

void PWM::toggle(int r) {
	char command[255];
	sprintf(command, "echo %d > %s/%s/run", r, PWM_PATH, pwms[pwmNr].pwm_name);
	system(command);
	run = r;
}

void PWM::start() {
	toggle(1);
}

void PWM::stop() {
	toggle(0);
}

int PWM::isRunning() {
	return run;
}
