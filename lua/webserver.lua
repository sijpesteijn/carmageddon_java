print("Setting up web server...")

local client_conn
local request_prefix = "http_request:"
local response_prefix = "http_response:"

function getResponse(data) 
    response_start = string.find(data, response_prefix) + string.len(response_prefix)
    return string.sub(data, response_start)
end

uart.on("data","\0", 
    function(data)
        if string.find(data, response_prefix) ~= nil then
            response = getResponse(data)
            client_conn:send(response);
            client_conn:close();
            collectgarbage();
        end
    end, 1) 

srv=net.createServer(net.TCP)
srv:listen(8080,function(conn)
    conn:on("receive", function(client,request)
        client_conn=client
        uart.write(0, request_prefix..request)
        tmr.alarm(1, carma_cfg.http_timeout, tmr.ALARM_SINGLE, function() 
            buf="HTTP/1.0 555\r\nContent-Type: text/html\r\n\r\n555 - Beaglebone is not responding."
            client_conn:send(buf);
            client_conn:close();
            collectgarbage();
        end)
    end)
end)
