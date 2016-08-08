print("Setting up web server...")

local client_conn

uart.on("data","\0", 
    function(data)
        if string.find(data, "http_response") ~= nil then
            print("Response recieved: "..data)
            buf="HTTP/1.0 200\r\nContent-Type: text/html\r\n\r\n"..data
            client_conn:send(buf);
            client_conn:close();
            collectgarbage();
        end
    end, 1) 

srv=net.createServer(net.TCP)
srv:listen(8080,function(conn)
    conn:on("receive", function(client,request)
        client_conn=client
        uart.write(0, "http_request:"..request.."\0")
    end)
end)
