led1 = 3
led2 = 4
gpio.mode(led1, gpio.OUTPUT)
gpio.mode(led2, gpio.OUTPUT)

isOdd = false
cnt = 20
while cnt > 0 do
    if isOdd then
        isOdd = false
        gpio.write(led1, gpio.HIGH);
        gpio.write(led1, gpio.LOW);
    else
        isOdd = true
        gpio.write(led2, gpio.HIGH);
        gpio.write(led2, gpio.LOW);
    end
    
    cnt = cnt - 1
end
