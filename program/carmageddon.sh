#!/bin/bash

mount /dev/mmcblk0p1 /media
export SLOTS=/sys/devices/bone_capemgr.9/slots

echo 'am33xx_pwm' > $SLOTS
echo 'bone_pwm_P9_22' > $SLOTS
echo 'bone_pwm_P9_42' > $SLOTS
echo 'BB-UART4' > $SLOTS

stty -F /dev/ttyO4 115200 raw -echo