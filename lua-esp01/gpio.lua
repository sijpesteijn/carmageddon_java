pin2=4  -- gpio2
pin0=3 -- gpio0
lighton=0

gpio.mode(pin0,gpio.OUTPUT)
gpio.mode(pin2,gpio.OUTPUT)

tmr.alarm(1, 1000, 1, function()
    if lighton==0 then
        lighton=1
        gpio.write(pin2,gpio.HIGH)
        gpio.write(pin0,gpio.LOW)
        print("GPIO: "..lighton)
    else
        lighton=0
        gpio.write(pin2,gpio.LOW)
        gpio.write(pin0,gpio.HIGH)
        print("GPIO: "..lighton)
    end
  end)
