(function () {
    'use strict';

    app.controller('autonomousCtrl', autonomousController).directive('autonomous', autonomousDirective);

    autonomousController.$inject = ['$scope', '$resource', '$interval', '$timeout', 'websocketFactory'];

    function autonomousController($scope, $resource, $interval, $timeout, websocketFactory) {
        var websocket;
        $scope.msgs = [];
        $scope.lowerHSVMin;
        $scope.lowerHSVMax;
        $scope.upperHSVMin;
        $scope.upperHSVMax;
        $scope.viewtype;
        $scope.framerate = 5;
        $scope.racing = false;
        $scope.showSettings = false;
        $scope.tab = 'general';
        $scope.baw = false;
        var updateTimeout = angular.undefined;
        var framerateInterval;
        var lastLookout = angular.undefined;
        var image = document.getElementById("img");

        $scope.startRace = function () {
            $resource('./rest/autonomous/start').save({}, {},
                function (success) {
                    $scope.msgs = [];
                    $scope.racing = true;
                    // $interval.cancel(statusInterval);
                },
                function (error) {
                    $scope.racing = false;
                    console.error('mode update failed', error);
                });
        };

        $scope.stopRace = function () {
            $resource('./rest/autonomous/stop').save({}, {},
                function (success) {
                    $scope.racing = false;
                },
                function (error) {
                    $scope.racing = false;
                    console.error('mode update failed', error);
                });
        };

        $scope.readyToRace = function() {
            if (lastLookout !== angular.undefined) {
                var status = lastLookout.status;
                if ( status ===  'READY_TO_RACE' ) {
                    $scope.racing = false;
                    return true;
                }
            }
            return false;
        };

        function statusUpdate() {
            websocket.sendMessage('status');
        }

        function buildHsv(hsv) {
            return 'hsv('+ hsv.hue + ',' + Math.round(hsv.saturation / (255/100)) + '%,' + Math.round(hsv.brightness / (255/100)) + '%)';
        }

        function getSettings() {
            $resource('./rest/autonomous/settings').get({}, {},
                function (settings) {
                    $scope.lowerHSVMin = buildHsv(settings.trafficLight.lowerHSVMin);
                    $scope.lowerHSVMax = buildHsv(settings.trafficLight.lowerHSVMax);
                    $scope.upperHSVMin = buildHsv(settings.trafficLight.upperHSVMin);
                    $scope.upperHSVMax = buildHsv(settings.trafficLight.upperHSVMax);
                    $scope.viewType = settings.viewType;
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        }

        $scope.updateViewType = function(viewType) {
            $scope.viewType = viewType;
            $scope.updateSettings();
        };

        $scope.updateSettings = function() {
            $resource('./rest/autonomous/settings').save({},
                {
                    viewType: $scope.viewType,
                    trafficLight: {
                        lowerHSVMin: getHsv($scope.lowerHSVMin),
                        lowerHSVMax: getHsv($scope.lowerHSVMax),
                        upperHSVMin: getHsv($scope.upperHSVMin),
                        upperHSVMax: getHsv($scope.upperHSVMax)
                    }
                },
                function (success) {
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        };

        function getHsv(hsv) {
            var splits = hsv.split(',');
            var result = {
                hue: splits[0].split('(')[1],
                saturation: Math.round(splits[1].substring(0,splits[1].length-1)*(255/100)),
                brightness: Math.round(splits[2].substring(0,splits[2].length-2)*(255/100))
            };
            return result;
        }

        $scope.options = {
            format: 'hsv',
            hue: true,
            swatch: true
        };

        $scope.eventApi = {
            onChange: function(api, color, $event) {
                var id = api.getElement().attr('id');
                if (id === 'lowerHSVMin') {
                    $scope.lowerHSVMin = color;
                } else if (id === 'lowerHSVMax') {
                    $scope.lowerHSVMax = color;
                } else if (id === 'upperHSVMin') {
                    $scope.upperHSVMin = color;
                } else {
                    $scope.upperHSVMax = color;
                }
                if (updateTimeout != null) {
                    $timeout.cancel(updateTimeout);
                }
                updateTimeout = $timeout($scope.updateSettings, 500);
            }
        };

        $scope.updateFramerate = function(framerate) {
            $scope.framerate = framerate;
            console.log('framerate ' + $scope.framerate);
            if (framerateInterval != angular.undefined) {
                $interval.cancel(framerateInterval);
            }
            framerateInterval = $interval(statusUpdate, 1000/$scope.framerate);
        };

        $scope.getFramerate = function () {
            return $scope.framerate;
        };

        $scope.$on('$destroy', function () {
            console.debug('destroying autonomous controller');
            websocket.closeConnection();
        });

        websocket = websocketFactory.create('autonomous/status');
        getSettings();
        $scope.updateFramerate($scope.framerate);

        websocket.onMessage(function (message) {
            if (message.data !== 'pong') {
                if (message.data.indexOf('{') == 0) {
                    lastLookout = angular.fromJson(message.data);
                    $scope.readyToRace();
                    if ($scope.msgs.length > 0) {
                        var last = $scope.msgs[$scope.msgs.length - 1];
                        if (last.msg.indexOf(lastLookout.status) == 0) {
                            last.count++;
                        } else {
                            $scope.msgs.push({msg: lastLookout.status, count: 1});
                        }
                    } else {
                        $scope.msgs.push({msg: lastLookout.status, count: 1});
                    }
                } else {
                    // console.log('New snapshot');
                    image.src = 'data:image/png;base64,' + message.data;
                }
            }
        });
    }

    function autonomousDirective() {
        return {
            templateUrl: './scripts/directives/autonomous/autonomous.html',
            controller: 'autonomousCtrl',
            replace: true
        }
    }

})();