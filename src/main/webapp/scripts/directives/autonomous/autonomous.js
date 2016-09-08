(function () {
    'use strict';

    app.controller('autonomousCtrl', autonomousController).directive('autonomous', autonomousDirective);

    autonomousController.$inject = ['$scope', '$resource', 'websocketFactory'];

    function autonomousController($scope, $resource, websocketFactory) {
        $scope.racing = false;
        $scope.connected = false;
        var websocket = websocketFactory.create('autonomous/status');
        $scope.msgs = [];

        websocket.onMessage(function (message) {
            if (message.data !== 'pong') {
                console.log('autonomous status: ' + message.data);
                var info = angular.fromJson(message.data);
                $scope.connected = info.connected;
                $scope.racing = info.racing;
                if ($scope.msgs.length > 0) {
                    var last = $scope.msgs[$scope.msgs.length-1];
                    if (last.msg.indexOf(info.message) == 0) {
                        last.count++;
                    } else {
                        $scope.msgs.push({msg: info.message, count: 1 });
                    }
                } else {
                    $scope.msgs.push({msg: info.message, count: 1 });
                }
            }
        });

        $scope.startRace = function () {
            $resource('./rest/car/autonomous').save({}, {},
                function (success) {
                    $scope.racing = true;
                },
                function (error) {
                    console.error('mode update failed', error);
                });

        };
    }

    function autonomousDirective() {
        return {
            templateUrl: './scripts/directives/autonomous/autonomous.html',
            controller: 'autonomousCtrl',
            replace: true
        }
    }

})();