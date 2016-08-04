print("Setting up web server...")

srv=net.createServer(net.TCP)
srv:listen(8080,function(conn)
    conn:on("receive", function(client,request)
        local method = string.sub(request,0,string.find(request, " ")-1)
        local target = string.sub(request,string.find(request," /")+2,string.find(request,"HTTP/")-2)

        print("!"..method.."! !"..target.."!")

        buf="HTTP/1.0 200\r\nContent-Type: text/html\r\n\r\nJa?"
        client:send(buf);

        client:close();
        collectgarbage();
    end)
end)
