'use strict';

var app = angular.module('carmageddon', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngRoute',
    'ngWebSocket'
]);

app.run(function ($rootScope) {
    $rootScope.carMode = 'disabled';
    $rootScope.settings = {
        throttleLimit: 20
    }
});


app.factory('websocketFactory', function ($websocket, $location, $interval, $timeout) {

    function websocket(ep) {
        var connection = null;
        var connected = false;
        var pinger;
        var endpoint;

        endpoint = ep;
        var splits = $location.absUrl().split('/');
        var url = 'ws://' + splits[2] + '/' + splits[3] + '/' + endpoint;
        connection = $websocket(url, undefined , {
            binaryType: "arraybuffer"
        });

        connection.onOpen(function () {
            connected = true;
            startPinger();
        });

        connection.onError(function (error) {
            connected = false;
            stopPinger();
            reconnect();
        });

        function startPinger() {
            pinger = $interval(function () {
                connection.send('ping');
            }, 1000)
        }

        function stopPinger() {
            $interval.cancel(pinger);
        }

        function reconnect() {
            $timeout(function () {
                connect(endpoint)
            }, 1000);
        }

        this.sendMessage = function(message) {
            connection.send(message);
        };

        this.onMessage = function(callback) {
            connection.onMessage(function (message) {
                callback(message);
            })
        };

        this.closeConnection = function() {
            connection.close();
        }
    }

    return {
        create: function (endpoint) {
            return new websocket(endpoint);
        }
    }

});