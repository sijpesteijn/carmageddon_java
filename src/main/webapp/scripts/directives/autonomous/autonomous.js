(function () {
    'use strict';

    app.controller('autonomousCtrl', autonomousController).directive('autonomous', autonomousDirective);

    autonomousController.$inject = ['$scope', '$resource', 'websocketFactory'];

    function autonomousController($scope, $resource, websocketFactory) {
        var websocket = websocketFactory.create('autonomous/status');
        $scope.msgs = [];
        var lastLookout = angular.undefined;

        websocket.onMessage(function (message) {
            if (message.data !== 'pong') {
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
            $resource('./rest/car/autonomous').save({}, {},
                function (success) {
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        };

        $scope.readyToRace = function() {
            if (lastLookout !== angular.undefined) {
                var status = lastLookout.status;
                return status ===  'CAR_READY_TO_RACE';
            }
            return false;
        }
    }

    function autonomousDirective() {
        return {
            templateUrl: './scripts/directives/autonomous/autonomous.html',
            controller: 'autonomousCtrl',
            replace: true
        }
    }

})();