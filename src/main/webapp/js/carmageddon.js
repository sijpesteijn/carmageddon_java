
function createRequest() {
    var result = null;
    if (window.XMLHttpRequest) {
        // FireFox, Safari, etc.
        result = new XMLHttpRequest();
    }
    else if (window.ActiveXObject) {
        // MSIE
        result = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return result;
}

var req = createRequest(); // defined above
// Create the callback:
req.onreadystatechange = function() {
    if (req.readyState != 4) return; // Not there yet
    if (req.status != 200) {
        // Handle request failure here...
        return;
    }
    // Request successful, read the response
    var resp = req.responseText;
    // ... and use it as needed by your app.
};

console.log("touchscreen is", VirtualJoystick.touchScreenAvailable() ? "available" : "not available");
var joystick	= new VirtualJoystick({
    container	: document.getElementById('container'),
    baseX		: 400,
    baseY		: 400,
    mouseSupport	: true,
    limitStickTravel: true,
    stickRadius: 150
});
joystick.addEventListener('touchStart', function(){
    console.log('down');
});
joystick.addEventListener('touchEnd', function(){
    console.log('up');
});

function postThrottle(throttle) {
    req.open("POST", "./rest/car/engine/" + throttle, true);
    req.send();
}

var currAngle = 0;
var currThrottle = 0;
var limitThrottle = 15;

function setThrottleLimit(limit) {
    limitThrottle = limit;
}

setInterval(function(){
    var angle = Math.round(joystick.deltaX());
    if (angle != currAngle) {
        req.open("POST", "./rest/car/steer/" + angle, true);
        req.send();
        currAngle = angle;
    }
    var throttle = Math.round(joystick.deltaY());
    if (limitThrottle > 0 && throttle > limitThrottle) {
        throttle = limitThrottle;
        if(currThrottle != throttle) {
            currThrottle = throttle;
            postThrottle(throttle);
        }
    } else if (limitThrottle > 0 && throttle < -limitThrottle) {
        throttle = -limitThrottle;
        if (currThrottle != throttle) {
            currThrottle = throttle;
            postThrottle(throttle)
        }
    } else {
        if (throttle != currThrottle) {
            postThrottle(throttle);
            currThrottle = throttle;
        }
    }
}, 1000);

function stop() {
    req.open("POST", "./rest/car/stop", true);
    req.send();
}

function SetupStatusWebSocket()
{
    if ("WebSocket" in window)
    {
        // Let us open a web socket
        var ws = new WebSocket("ws://localhost:8082/carmageddon/status");

        ws.onopen = function()
        {
            // Web Socket is connected, send data using send()
        };

        ws.onmessage = function (evt)
        {
            var msg = JSON.parse(evt.data);
            console.log("Message is received..." + msg);
            var value = msg.throttle;
            var outputEl = document.getElementById("throttle");
            if (value == undefined) {
                value = msg.angle;
                outputEl = document.getElementById("angle");
            }
            outputEl.innerHTML = value;
        };

        ws.onclose = function()
        {
            // websocket is closed.
            console.log("Connection is closed...");
        };
    }

    else
    {
        // The browser doesn't support WebSocket
        console.log("WebSocket NOT supported by your Browser!");
    }
}

SetupStatusWebSocket();