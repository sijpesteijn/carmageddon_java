print("Setting up i2c bus with Beaglebone black")

i2c.start(0)
i2c.address(0,0x27, i2c.TRANSMITTER)
i2c.stop(0)