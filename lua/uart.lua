uart.setup(0,115200,8,0,1)

print ("uart.setup(0,115200,8,0,1) finished")
print ("waiting for serial data....")

function reset()
    node.restart()
end

function status() 
    print(cjson.encode(carma_cfg))
end
function cmd_1(value)
  print("cmd_1("..value..") received")
end

function cmd_2(value)
  print("cmd_2("..value..") received")
end
