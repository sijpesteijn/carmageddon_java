/*
 * Pwm.h
 *
 *  Created on: Jun 9, 2016
 *      Author: gijs
 */

#include <unistd.h>
#include <syslog.h>
#include <stdlib.h>
#include <string.h>
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
	duty = 0;
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
	char filename[60] = PWM_PATH;
	strcat(filename, pwms[pwmNr].pwm_name);

	if (access(filename, F_OK) == -1) {
		syslog(LOG_ERR, "Could not open file: %s", filename);
		return 1;
	}
	return 0;
}

void PWM::setPolarity(int p) {
	polarity = p;
}

int PWM::getPolarity() {
	return polarity;
}

void PWM::setPeriod(int p) {
	period = p;
}

int PWM::getPeriod() {
	return period;
}

void PWM::setDuty(int d) {
	duty = d;
}

int PWM::getDuty() {
	return duty;
}
