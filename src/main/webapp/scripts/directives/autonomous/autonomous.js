(function () {
    'use strict';

    app.controller('autonomousCtrl', autonomousController).directive('autonomous', autonomousDirective);

    autonomousController.$inject = ['$scope', '$resource', '$timeout', 'websocketFactory'];

    function autonomousController($scope, $resource, $timeout, websocketFactory) {
        var websocket = websocketFactory.create('autonomous/status');
        $scope.msgs = [];
        $scope.lowerRGBMin = {red:10, green:100, blue:100};
        $scope.lowerRGBMax = {red:12, green:255, blue:255};
        $scope.upperRGBMin = {red:0,  green:100, blue:100};
        $scope.upperRGBMax = {red:9, green:255, blue:255};
        $scope.showSettings = false;
        var updateInterval;
        var lastLookout = angular.undefined;
        var canvas = document.getElementById("snapshot");
        var image = document.getElementById("img");
        var context = canvas.getContext("2d");

        function init() {
            getSettings();
        }

        websocket.onMessage(function (message) {
            if (!(typeof message.data === 'string')) {
                console.log('BINAIRY');
                var blob = message.data;

                var bytes = new Uint8Array(blob);
                var src = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";
                console.log('Received: ' + src);
                image.src = src;
                // var imageData = context.createImageData(300,150);
                //
                // for (var i=8; i<imageData.data.length; i++) {
                //     imageData.data[i] = bytes[i];
                // }
                // context.putImageData(imageData, 0, 0);
                //
                // var img = document.createElement('img');
                // img.height = canvas.height;
                // img.width = canvas.width;
                // img.src = canvas.toDataURL("image/jpg");
            }
            if ((typeof message.data === 'string') && message.data !== 'pong') {
                lastLookout = angular.fromJson(message.data);
                if ($scope.msgs.length > 0) {
                    var last = $scope.msgs[$scope.msgs.length-1];
                    if (last.msg.indexOf(lastLookout.status) == 0) {
                        last.count++;
                    } else {
                        $scope.msgs.push({msg: lastLookout.status, count: 1 });
                    }
                } else {
                    $scope.msgs.push({msg: lastLookout.status, count: 1 });
                }
            }
        });

        $scope.startRace = function () {
            $resource('./rest/autonomous/start').save({}, {},
                function (success) {
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
                    lowerRGBMin: $scope.lowerRGBMin,
                    lowerRGBMax: $scope.lowerRGBMax,
                    upperRGBMin: $scope.upperRGBMin,
                    upperRGBMax: $scope.upperRGBMax
                },
                function (success) {
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        }

        $scope.$watchCollection('lowerRGBMin', function() {
            if ($scope.lowerRGBMin != angular.undefined) {
                if (updateInterval != null) {
                    $timeout.cancel(updateInterval);
                }
                updateInterval = $timeout(updateSettings, 500);
            }
        }, true);
        $scope.$watchCollection('lowerRGBMax', function() {
            if ($scope.lowerRGBMax != angular.undefined) {
                if (updateInterval != null) {
                    $timeout.cancel(updateInterval);
                }
                updateInterval = $timeout(updateSettings, 500);
            }
        }, true);
        $scope.$watchCollection('upperRGBMin', function() {
            if ($scope.upperRGBMin != angular.undefined) {
                if (updateInterval != null) {
                    $timeout.cancel(updateInterval);
                }
                updateInterval = $timeout(updateSettings, 500);
            }
        }, true);
        $scope.$watchCollection('upperRGBMax', function() {
            if ($scope.upperRGBMax != angular.undefined) {
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