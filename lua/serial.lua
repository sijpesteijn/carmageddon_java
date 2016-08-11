print("Setting up serial communication with the Beaglebone black")

uart.on("data","\0", 
    function(data)
        if string.find(data, "http_response") ~= nil then
            print("Data recieved: "..data)
        end
    end, 1) 

--uart.write(0, "Hello world\n")
