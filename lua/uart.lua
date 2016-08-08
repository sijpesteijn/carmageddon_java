uart.setup(0,115200,8,0,1)
print("uart.setup(0,115200,8,0,1) finished")

function reset()
    node.restart()
end

function status() 
    print(cjson.encode(carma_cfg))
end

function alive()
    print(1)
end

function http_response(html)
    buf="HTTP/1.0 200\r\nContent-Type: text/html\r\n\r\nJa?"
    client:send(buf);

    client:close();
    collectgarbage();
end

