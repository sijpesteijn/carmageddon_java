print("Setting up i2c bus with 8x8 matrix")
scl=4 -- gpio2
sda=3 -- gpio0
busid=0 -- the one and only i2c bus
addr=0x70 -- address of the 8x8 led matrix
fill_bar=true -- fill the bar with green leds uptill peak led
orange_peak=false -- peak led orange or red

-- write registers
function write_i2c(dev_addr, value)
     i2c.start(busid)
     i2c.address(busid, dev_addr, i2c.TRANSMITTER)
     i2c.write(busid,value)
     i2c.stop(busid)
end

-- set led value
function set_led(reg, value)
     i2c.start(busid)
     i2c.address(busid, addr, i2c.TRANSMITTER)
     i2c.write(busid, reg)
     i2c.write(busid, value)
     i2c.stop(busid)
end

i2c.setup(busid,scl,sda,i2c.SLOW)

write_i2c(addr,0x21) -- start oscillator
write_i2c(addr,0x81) -- display on, blinking off
write_i2c(addr,0xe7) -- full brightness

-- clear the display
for reg=0x00, 0x10, 1
do
     i2c.start(busid)
     i2c.address(busid, addr, i2c.TRANSMITTER)
     i2c.write(busid,reg)
     i2c.write(busid,0x00)
     i2c.stop(busid)
end

function set_bar(bar_nr, level) 
 local leds = 0
 local peak = 0
 local mask = 0x01
 if level > 0 then
     for led=0, level-1, 1
     do
        leds = bit.bor(leds,mask)
        peak = bit.band(leds,mask)
        mask = bit.lshift(mask,1)
     end
 end
 if fill_bar then
    set_led(bar_nr*2, leds) -- set green leds
 elseif orange_peak then
    set_led((bar_nr*2), peak)
 end
 if orange_peak then
    set_led((bar_nr*2)+1, peak) -- set orange peak led
 else
    local fill = bit.bxor(leds,peak)
    set_led(bar_nr*2, fill)
    set_led((bar_nr*2)+1, peak)
 end
end

tmr.alarm(1, 500, 1, function()
    for bar=0,7,1 do
        set_bar(bar, math.random()*9)
    end
  end)
