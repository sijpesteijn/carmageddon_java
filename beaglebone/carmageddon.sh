#!/bin/bash

ifdown wlan0
ifup wlan0

#export JAVA_HOME=/usr/local/java
#export PATH=$PATH:$JAVA_HOME/bin
export SLOTS=/sys/devices/bone_capemgr.9/slots

mount /dev/mmcblk0 /mnt/media/

echo 'am33xx_pwm' > $SLOTS
echo 'bone_pwm_P9_22' > $SLOTS
echo 'bone_pwm_P9_42' > $SLOTS