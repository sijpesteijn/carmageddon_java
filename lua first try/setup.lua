function readConfig()
    local cfg = {}
    local props = file.open('config.properties',"r")
    if props ~= nil then
        local content = file:read()
        print(content)
        ok, json = pcall(cjson.encode, content)
        if ok then
          print(json)
        else
          print("failed to encode!")
        end
        
--        cfg = cjson.decode(content)
--        for k,v in pairs(cfg) do print(k,v) end
    end
    file.close()
    return cfg
end

carma_cfg = readConfig()
