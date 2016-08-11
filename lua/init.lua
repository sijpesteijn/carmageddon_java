print("Initializing ESP8266...")

if file.exists("config.json") then
    local props = file.open("config.json","r")
    local json = file.read()
    carma_cfg = cjson.decode(json)
end

if not file.exists("config.json") then
    carma_cfg = {}
    carma_cfg.wifi_mode = wifi.SOFTAP
    carma_cfg.ap = {}
    carma_cfg.ap.ssid = "Carmageddon"
    carma_cfg.ap.pwd = "vlinderfeest"
    carma_cfg.ap.dhcp = "192.168.3.1"
    carma_cfg.ap.gateway = "192.168.3.1"
    carma_cfg.ap.netmask = "255.255.255.0"
    carma_cfg.ap.connected = "false"
    carma_cfg.internet = {}
    carma_cfg.internet.ssid = "Gallie"
    carma_cfg.internet.pwd = "2014ACD3467"
    carma_cfg.internet.connected = "false"
end

dofile("wifisetup.lua")
dofile("uart.lua")
dofile("webserver.lua")

-- some examples
--dofile("gpio.lua")  -- use the gpio pins to trigger leds
--dofile("8x8matrix.lua") -- use the i2c bus to trigger led matrix
