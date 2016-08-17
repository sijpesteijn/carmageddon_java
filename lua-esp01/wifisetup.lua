-- wifi.STATIONAP = Create access point and connect to access point.
-- wifi.STATION = Connect to access point.
-- wifi.SOFTAP = Create access point.

function printStatus()
    if carma_cfg.internet.connected=="true" and (carma_cfg.wifi_mode == wifi.STATIONAP or carma_cfg.wifi_mode == wifi.STATION) then
        ssid, password, bssid_set, bssid=wifi.sta.getconfig()
        print("Internet connection status:")
        print("SSID: "..ssid)
        print("Password: "..password)
        print("BSSID_set: "..bssid_set)
        print("BSSID: "..bssid)
        ssid, password, bssid_set, bssid=nil, nil, nil, nil
    end
    if carma_cfg.wifi_mode == wifi.STATIONAP or carma_cfg.wifi_mode == wifi.SOFTAP then
        print("Accesspoint Configuration")
        print("MAC: "..wifi.ap.getmac())
        print("IP: "..wifi.ap.getip())
        print("BROADCAST: "..wifi.ap.getbroadcast())
    end
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
            carma_cfg.internet.connected="true"
         else 
            print("Conected to Router Timeout")
            carma_cfg.internet.connected="false"
         end
         printStatus()
         gpio.write(0,gpio.LOW);
         cnt = nil;cfg=nil;str=nil;ssidTemp=nil;
         collectgarbage()
     end 
   end)
    return cnt
end

wifi.setphymode(wifi.PHYMODE_G)
if carma_cfg.wifi_mode == wifi.STATIONAP then
    print("Wifi mode: STATIONAP")
    wifi.setmode(wifi.STATIONAP)
    cfg = {}
    cfg.ssid=carma_cfg.ap.ssid
    cfg.pwd=carma_cfg.ap.pwd
    wifi.ap.config(cfg)
    cfg={}
    cfg.dhcp=carma_cfg.ap.dhcp
    cfg.gateway=carma_cfg.ap.gateway
    cfg.netmask=carma_cfg.ap.netmask
    wifi.ap.setip(cfg)
    carma_cfg.ap.connected = "true";
    connectAccessPoint()
elseif carma_cfg.wifi_mode == wifi.SOFTAP then
    print("Wifi mode: SOFTAP (Create an access point "..carma_cfg.ap.ssid..")")
    wifi.setmode(wifi.SOFTAP)
    cfg = {}
    cfg.ssid=carma_cfg.ap.ssid
    cfg.pwd=carma_cfg.ap.pwd
    wifi.ap.config(cfg)
    cfg={}
    cfg.dhcp=carma_cfg.ap.dhcp
    cfg.gateway=carma_cfg.ap.gateway
    cfg.netmask=carma_cfg.ap.netmask
    wifi.ap.setip(cfg)
    printStatus()
    carma_cfg.internet.connected = "false";
    carma_cfg.ap.connected = "true";
elseif carma_cfg.wifi_mode == wifi.STATION then
    print("Wifi mode: STATION (Connect to acces point "..carma_cfg.internet.ssid..")")
    wifi.setmode(wifi.STATION)
    carma_cfg.ap.connected="false"
    connectAccessPoint()
end
