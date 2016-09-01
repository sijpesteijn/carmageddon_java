(function() {
    'use strict';

        app.factory('connectionCheckWebSocket', function ($websocket, $location) {

            var connection = $websocket($location.absUrl().replace(/http/g,'ws') + 'check');

            function sendMessage(message) {
                connection.send(message);
            }

            return {
                sendMessage : function (message) {
                    sendMessage(message);
                },
                onMessage : function (callback) {
                    connection.onMessage(function (message) {
                        callback(message);
                    })
                },
                onError : function (callback) {
                    connection.onError(function (error) {
                        callback(error);
                    })
                }
            }
        })
    }
)();