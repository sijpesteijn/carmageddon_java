function printStatus()
    print("Heep:(bytes)"..node.heap());
    print("MAC:"..wifi.ap.getmac().."\r\nIP:"..wifi.ap.getip());
end

function connectAccessPoint()
    wifi.sta.config(carma_cfg.internet.ssid,carma_cfg.internet.pwd)
    wifi.sta.connect()
    local cnt = 0
    gpio.mode(0,gpio.OUTPUT);
    tmr.alarm(0, 1000, 1, function() 
     if (wifi.sta.getip() == nil) and (cnt < 20) then 
         print("Trying Connect to Router, Waiting...")
         cnt = cnt + 1 
          if cnt%2==1 then gpio.write(0,gpio.LOW);
          else gpio.write(0,gpio.HIGH); end
     else 
         tmr.stop(0);
         if (cnt < 20) then print("Conected to Router\r\nMAC:"..wifi.sta.getmac().."\r\nIP:"..wifi.sta.getip())
             else print("Conected to Router Timeout")
         end
         gpio.write(0,gpio.LOW);
         cnt = nil;cfg=nil;str=nil;ssidTemp=nil;
         collectgarbage()
     end 
   end)
    return cnt
end

if carma_cfg.wifi_mode == wifi.STATIONAP then
    print("Wifi mode: STATIONAP")
    wifi.setmode(wifi.STATIONAP)
    wifi.ap.config(carma_cfg.ap)
    wifi.ap.setip(carma_cfg.internet)
    print("Access point IP:"..wifi.ap.getip());
    print("SSID:"..carma_cfg.ap.ssid);
    print("PWD:"..carma_cfg.ap.pwd);
    connectAccessPoint()
elseif carma_cfg.wifi_mode == wifi.SOFTAP then
    print("Wifi mode: SOFTAP")
    wifi.setmode(wifi.SOFTAP)
    wifi.ap.setip(carma_cfg.internet)
    print("Access point IP:"..wifi.ap.getip());
    print("SSID:"..carma_cfg.ap.ssid);
    print("PWD:"..carma_cfg.ap.pwd);
elseif carma_cfg.wifi_mode == wifi.STATION then
    print("Wifi mode: STATION")
    wifi.setmode(wifi.STATION)
    wifi.ap.config(carma_cfg.ap)
    connectAccessPoint()
end