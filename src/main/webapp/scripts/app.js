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
        throttleLimit: 40
    }
});


app.factory('websocketFactory', function ($websocket, $location, $interval) {

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
            console.log('Connection stopped' + error);
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
            console.log('Connection closed');
        }
    }

    return {
        create: function (endpoint) {
            return new websocket(endpoint);
        }
    }

});

app.factory('settingsFactory', function($timeout, $resource, $q) {
    var updateTimeout;
    var settings = $q.defer();

    function getSettings() {


        $resource('./rest/autonomous/settings').get({}, {},
            function (data) {
                var newSettings = data;
                newSettings.trafficLightSettings.lowerHSVMin = buildHsv(data.trafficLightSettings.lowerHSVMin);
                newSettings.trafficLightSettings.lowerHSVMax = buildHsv(data.trafficLightSettings.lowerHSVMax);
                newSettings.trafficLightSettings.upperHSVMin = buildHsv(data.trafficLightSettings.upperHSVMin);
                newSettings.trafficLightSettings.upperHSVMax = buildHsv(data.trafficLightSettings.upperHSVMax);
                settings.resolve(newSettings);
            },
            function (error) {
                console.error('mode update failed', error);
            });
        return settings;
    }

    function updateSettings() {

        var newSettings = settings;
        newSettings.trafficLightSettings.lowerHSVMin = getHsv(newSettings.lowerHSVMin);
        newSettings.trafficLightSettings.lowerHSVMax = getHsv(newSettings.lowerHSVMax);
        newSettings.trafficLightSettings.upperHSVMin = getHsv(newSettings.upperHSVMin);
        newSettings.trafficLightSettings.upperHSVMax = getHsv(newSettings.upperHSVMax);
        $resource('./rest/autonomous/settings').save({},
            newSettings,
            function (success) {
            },
            function (error) {
                console.error('mode update failed', error);
            });
    }

    function getHsv(hsv) {
        var splits = hsv.split(',');
        var result = {
            hue: splits[0].split('(')[1],
            saturation: Math.round(splits[1].substring(0,splits[1].length-1)*(255/100)),
            brightness: Math.round(splits[2].substring(0,splits[2].length-2)*(255/100))
        };
        return result;
    }

    function buildHsv(hsv) {
        return 'hsv('+ hsv.hue + ',' + Math.round(hsv.saturation / (255/100)) + '%,' + Math.round(hsv.brightness / (255/100)) + '%)';
    }

    function delaySettingsUpdate(settings) {
        this.settings = settings;
        if (updateTimeout != null) {
            $timeout.cancel(updateTimeout);
        }
        updateTimeout = $timeout(updateSettings, 500);
    }


    return {
        getSettings: function () {
            return getSettings().promise;
        },
        updateSettings: function (settings) {
            updateSettings(settings);
        }
    }
});