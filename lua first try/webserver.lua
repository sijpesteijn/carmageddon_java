print("Setting up web server...")

function ends(String,End)
   return End=='' or string.sub(String,-string.len(End))==End
end

function sendResource(filename, contentType, client)
   local resource = file.open(filename,"r")
    if resource ~= nil then
        html = file.read()
        file.close()
    end
    contentLength = string.len(html)
    buf="HTTP/1.0 200\r\nContent-Length: "..contentLength.."\r\nContent-Type: "..contentType.."\r\n\r\n"..html
    client:send(buf);
end

function resolveHtml(filename, content)
    if (filename == "wifi") then
        content=string.gsub(content,"${carma_cfg.ap.ssid}",carma_cfg.ap.ssid)
        content=string.gsub(content,"${wifi_mode}",carma_cfg.wifi_mode)
    end
    print(content)
    return content
end

function handleGet(client, request)
    local tgtfile = string.sub(request,string.find(request,"GET /")+5,string.find(request,"HTTP/")-2)
    print("!"..tgtfile.."!")
    if (ends(tgtfile, ".css") then
        sendResource(tgtfile, "text/css", client)
    elseif (ends(tgtfile, ".js") then
        sendResource(tgtfile, "application/javascript", client)
    else
        local index = file.open("index.html","r")
        if index ~= nil then
            html=file.read()
            file.close()
            if tgtfile == "" then tgtfile = "status" end  
            local f = file.open(tgtfile..".html","r")
            local content = tgtfile..".html Not found"
            if f ~= nil then
                content = file.read()
                content = resolveHtml(tgtfile, content)
                file.close()
            end
            html=string.gsub(html,"${content}",content)
            contentLength = string.len(html)
            buf="HTTP/1.0 200\r\nContent-Length: "..contentLength.."\r\nContent-Type: text/html\r\n\r\n"..html
            client:send(buf);
        end
    end
end

srv=net.createServer(net.TCP)
srv:listen(8080,function(conn)
    conn:on("receive", function(client,request)
        local method = string.sub(request,0,string.find(request, " ")-1)
        if method == "GET" then
            handleGet(client, request)
        elseif method == "POST" then
        else
            local contentType = "text/html"
            local html="Don't know how to handle: "..method
            local contentLength = string.len(html)
            buf="HTTP/1.0 500 SERVER ERROR\r\nContent-Length: "..contentLength.."\r\nContent-Type: "..contentType.."\r\n\r\n"..html
            client:send(buf);
        end

        client:close();
        collectgarbage();
    end)
end)
