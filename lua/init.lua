print("Initializing ESP8266...")

local props = file.open("config.json","r")
if props ~= nil then
    local json = file.read()
    carma_cfg = cjson.decode(json)
else
-- Loading defaults
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
--dofile("uart.lua")
dofile("serial.lua")
--dofile("webserver.lua")
--dofile("gpio.lua")
--dofile("8x8matrix.lua")
