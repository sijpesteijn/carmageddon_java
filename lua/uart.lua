
function reset()
    node.restart()
end

function getconfig() 
    print(cjson.encode(carma_cfg))
end

function setconfig(config)
    print("New config: "..config)
    file.open("config.json", "w+")
    file.write(config)
    file.close()
    node.restart()
end

function alive()
    print(1)
end
