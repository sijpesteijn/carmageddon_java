./esptool/esptool.py --port /dev/tty.usbserial-A7004kUk write_flash 0x00000 ./esp-12/nodemcu-master-14-modules-2016-08-15-17-05-44-float.bin 0x3fc000 ./esp-12/esp_init_data_default.bin -fm=dio -fs=32m