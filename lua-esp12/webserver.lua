print("Setting up web server...")

local client_conn = nil

function http_response(data)
    if (string.find(data, "RESPONSE_END") == 1) then
        client_conn:close()
        collectgarbage();
    else
        if (string.find(data, "RESPONSE_BODY") == 1) then
            client_conn:send("\r\n\r\n")
        else
            decoded = encoder.fromBase64(data)
            client_conn:send(decoded)
        end
    end
end

srv=net.createServer(net.TCP)
srv:listen(8080,function(conn)
    conn:on("receive", function(client,request)
        client_conn=client
        buf="HTTP/1.0 555\r\nContent-Type: text/html\r\n\r\n555 - Beaglebone is not responding."
        uart.write(0, "http_request:"..request)
        tmr.alarm(1, carma_cfg.http_timeout, tmr.ALARM_SINGLE, function() 
            client_conn:send(buf);
            client_conn:close();
            collectgarbage();
        end)
    end)
end)
