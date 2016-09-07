(function () {
    'use strict';

    app.controller('autonomousCtrl', autonomousController).directive('autonomous', autonomousDirective);

    autonomousController.$inject = ['$scope', 'websocketFactory'];

    function autonomousController($scope, websocketFactory) {
        $scope.racing = false;
        $scope.connected = false;
        var websocket = websocketFactory.create('status');

        websocket.onMessage(function (message) {
            if (message.data !== 'pong') {
                console.log('car status: ' + message.data);
                var car = angular.fromJson(message.data);
                $scope.connected = car.connected;
            }
        });

        $scope.startRace = function () {
            $scope.racing = true;
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