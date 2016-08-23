uart.setup(0, 115200, 8, 0, 1, 0)
function reset()
    node.restart()
end

function getconfig() 
    uart.write(0, cjson.encode(carma_cfg))
end

function setconfig(config)
    print("New config: "..config)
    file.open("config.json", "w+")
    file.write(config)
    file.close()
    node.restart()
end

function ping()
    print("pong")
end
