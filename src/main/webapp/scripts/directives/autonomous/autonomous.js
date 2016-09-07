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
                $scope.msgs.push(info.message);
            }
        });

        $scope.startRace = function () {
            $resource('./rest/car/autonomous').save({}, {},
                function (success) {
                    $scope.racing = true;
                },
                function (error) {
                    console.error('mode update failed', error);
                    $scope.msgs.push(error);
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