(function () {
    'use strict';

    app.controller('autonomousCtrl', autonomousController).directive('autonomous', autonomousDirective);

    autonomousController.$inject = ['$scope', '$resource', '$timeout', 'websocketFactory'];

    function autonomousController($scope, $resource, $timeout, websocketFactory) {
        var websocket = websocketFactory.create('autonomous/status');
        $scope.msgs = [];
        $scope.lowerHSVMin = {hue:10, saturation:100, value:100};
        $scope.lowerHSVMax = {hue:12, saturation:255, value:255};
        $scope.upperHSVMin = {hue:0,  saturation:100, value:100};
        $scope.upperHSVMax = {hue:9, saturation:255, value:255};
        $scope.showSettings = false;
        var updateInterval;
        var lastLookout = angular.undefined;
        var image = document.getElementById("img");

        function init() {
            getSettings();
        }

        websocket.onMessage(function (message) {
            if (message.data !== 'pong') {
                if (message.data.indexOf('{') == 0) {
                    lastLookout = angular.fromJson(message.data);
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
                    image.src = 'data:image/png;base64,' + message.data;
                }
            }
        });

        $scope.startRace = function () {
            $resource('./rest/autonomous/start').save({}, {},
                function (success) {
                    $scope.msgs = [];
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        };

        $scope.readyToRace = function() {
            if (lastLookout !== angular.undefined) {
                var status = lastLookout.status;
                return status ===  'READY_TO_RACE';
            }
            return false;
        };

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
            console.log('RGB');
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
                if (updateInterval != null) {
                    $timeout.cancel(updateInterval);
                }
                updateInterval = $timeout(updateSettings, 500);
            }
        }, true);
        $scope.$watchCollection('lowerHSVMax', function() {
            if ($scope.lowerHSVMax != angular.undefined) {
                if (updateInterval != null) {
                    $timeout.cancel(updateInterval);
                }
                updateInterval = $timeout(updateSettings, 500);
            }
        }, true);
        $scope.$watchCollection('upperHSVMin', function() {
            if ($scope.upperHSVMin != angular.undefined) {
                if (updateInterval != null) {
                    $timeout.cancel(updateInterval);
                }
                updateInterval = $timeout(updateSettings, 500);
            }
        }, true);
        $scope.$watchCollection('upperHSVMax', function() {
            if ($scope.upperHSVMax != angular.undefined) {
                if (updateInterval != null) {
                    $timeout.cancel(updateInterval);
                }
                updateInterval = $timeout(updateSettings, 500);
            }
        }, true);

        init();
    }

    function autonomousDirective() {
        return {
            templateUrl: './scripts/directives/autonomous/autonomous.html',
            controller: 'autonomousCtrl',
            replace: true
        }
    }

})();