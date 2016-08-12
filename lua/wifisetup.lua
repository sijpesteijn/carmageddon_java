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
          if cnt%2==1 then 
            gpio.write(0,gpio.LOW);
          else 
            gpio.write(0,gpio.HIGH); 
          end
     else 
         tmr.stop(0);
         if (cnt < 20) then 
            print("Conected to Router\r\nMAC:"..wifi.sta.getmac().."\r\nIP:"..wifi.sta.getip())
            carma_cfg.internet.connected = true
         else 
            print("Conected to Router Timeout")
            carma_cfg.internet.connected = false
         end
         gpio.write(0,gpio.LOW);
         cnt = nil;cfg=nil;str=nil;ssidTemp=nil;
         collectgarbage()
     end 
   end)
    tmr.unregister(0)
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
    carma_cfg.ap.connected = "true";
    connectAccessPoint()
elseif carma_cfg.wifi_mode == wifi.SOFTAP then
    print("Wifi mode: SOFTAP")
    wifi.setmode(wifi.SOFTAP)
    wifi.ap.setip(carma_cfg.internet)
    print("Access point IP:"..wifi.ap.getip());
    print("SSID:"..carma_cfg.ap.ssid);
    print("PWD:"..carma_cfg.ap.pwd);
    carma_cfg.ap.connected = "true";
elseif carma_cfg.wifi_mode == wifi.STATION then
    print("Wifi mode: STATION")
    wifi.setmode(wifi.STATION)
    wifi.ap.config(carma_cfg.ap)
    connectAccessPoint()
end
