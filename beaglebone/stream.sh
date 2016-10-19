#/bin/bash

cd /mnt/sdcard/mjpg-streamer/
./mjpg_streamer -i "./input_uvc.so -d /dev/video0 -n -f 15 -r 320x240" -o "./output_http.so -p 8090 -n -w ./www"