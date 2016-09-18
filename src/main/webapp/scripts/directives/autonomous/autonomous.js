(function () {
    'use strict';

    app.controller('autonomousCtrl', autonomousController).directive('autonomous', autonomousDirective);

    autonomousController.$inject = ['$scope', '$resource', '$interval', '$timeout', 'websocketFactory'];

    function autonomousController($scope, $resource, $interval, $timeout, websocketFactory) {
        var websocket;
        $scope.msgs = [];
        $scope.lowerHSVMin = {hue:10, saturation:100, value:100};
        $scope.lowerHSVMax = {hue:12, saturation:255, value:255};
        $scope.upperHSVMin = {hue:0,  saturation:100, value:100};
        $scope.upperHSVMax = {hue:9, saturation:255, value:255};
        $scope.framerate = 5;
        $scope.racing = false;
        $scope.showSettings = false;
        var updateTimeout = angular.undefined;
        var framerateInterval;
        var lastLookout = angular.undefined;
        var image = document.getElementById("img");

        websocket = websocketFactory.create('autonomous/status');
        getSettings();

        $scope.$watch('framerate', function (value) {
            console.log('framerate ' + value);
            // if (statusInterval != angular.undefined && value != angular.undefined ) {
                $interval.cancel(framerateInterval);
            // }
            framerateInterval = $interval(statusUpdate, 1000/$scope.framerate);
        }, true);

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
                    console.log('New snapshot');
                    image.src = 'data:image/png;base64,' + message.data;
                }
            }
        });

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

        function getSettings() {
            $resource('./rest/autonomous/settings').get({}, {},
                function (success) {
                    console.log(success);
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        }

        function updateSettings() {
            $resource('./rest/autonomous/settings').save({},
                {
                    lowerHSVMin: $scope.lowerHSVMin,
                    lowerHSVMax: $scope.lowerHSVMax,
                    upperHSVMin: $scope.upperHSVMin,
                    upperHSVMax: $scope.upperHSVMax
                },
                function (success) {
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        }

        $scope.$watchCollection('lowerHSVMin', function() {
            if ($scope.lowerHSVMin != angular.undefined) {
                if (updateTimeout != null) {
                    $timeout.cancel(updateTimeout);
                }
                updateTimeout = $timeout(updateSettings, 500);
            }
        }, true);
        $scope.$watchCollection('lowerHSVMax', function() {
            if ($scope.lowerHSVMax != angular.undefined) {
                if (updateTimeout != null) {
                    $timeout.cancel(updateTimeout);
                }
                updateTimeout = $timeout(updateSettings, 500);
            }
        }, true);
        $scope.$watchCollection('upperHSVMin', function() {
            if ($scope.upperHSVMin != angular.undefined) {
                if (updateTimeout != null) {
                    $timeout.cancel(updateTimeout);
                }
                updateTimeout = $timeout(updateSettings, 500);
            }
        }, true);
        $scope.$watchCollection('upperHSVMax', function() {
            if ($scope.upperHSVMax != angular.undefined) {
                if (updateTimeout != null) {
                    $timeout.cancel(updateTimeout);
                }
                updateTimeout = $timeout(updateSettings, 500);
            }
        }, true);

    }

    function autonomousDirective() {
        return {
            templateUrl: './scripts/directives/autonomous/autonomous.html',
            controller: 'autonomousCtrl',
            replace: true
        }
    }

})();