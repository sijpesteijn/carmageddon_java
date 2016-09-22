'use strict';

var app = angular.module('carmageddon', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngRoute',
    'ngWebSocket',
    'color.picker'
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

        this.connect = function() {
            try {
                connection = $websocket(url, undefined, {
                    binaryType: "arraybuffer"
                });
            } catch (err) {
                console.log('Can\'t connect to ' + url + ', err: ' + err);
            }
        };

        this.connect();

        connection.onOpen(function () {
            connected = true;
            startPinger();
        });

        function startPinger() {
            pinger = $interval(function () {
                connection.send('ping');
            }, 1000)
        }

        function stopPinger() {
            $interval.cancel(pinger);
        }

        connection.onError(function(error) {
            connected = false;
            stopPinger();
            // callback(error);
        });

        this.onMessage = function(callback) {
            connection.onMessage(function (message) {
                callback(message);
            })
        };

        this.sendMessage = function(message) {
            connection.send(message);
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