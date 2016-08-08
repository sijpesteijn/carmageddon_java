print("Setting up serial communication with the Beaglebone black")

uart.setup(0, 115200, 8, uart.PARITY_NONE, uart.STOPBITS_1, 1)

uart.on("data","\0", 
    function(data)
        print("Data recieved: "..data)
    end, 0) 

--uart.write(0, "Hello world\n")
