(function() {
    'use strict';

    app.controller('carStatusCtrl', carStatusController).directive('carStatus', carStatusDirective);

    carStatusController.$inject = ['$rootScope', '$scope','$resource', 'websocketFactory'];

    function carStatusController($rootScope, $scope, $resource, websocketFactory) {
        $scope.connected = false;
        $scope.car;
        $rootScope.carMode;
        $scope.modes = ['disabled','manual','autonomous'];

        var websocket = websocketFactory.create('car/status');

        websocket.onMessage(function (message) {
            if (message.data !== 'pong') {
                console.log("Car: " + message.data);
                $scope.car = angular.fromJson(message.data);
                $rootScope.carMode = $scope.car.mode;
            }
        });

        $scope.updateCarMode = function(carMode) {
            $scope.car.mode = carMode;
            $rootScope.carMode = $scope.car.mode;
            $resource('./rest/car/mode/:mode').save({
                    mode: $scope.car.mode
                }, {},
                function (success) {
                    // console.debug('mode send', success);
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        };

        $scope.blowHorn = function() {
            $resource('./rest/car/horn').save({}, {},
                function (success) {
                    // console.debug('mode send', success);
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        };

        $scope.$on('$destroy', function () {
            console.debug('destroying carstatus controller');
            websocket.closeConnection();
        });

    }

    function carStatusDirective() {
        return {
            templateUrl: './scripts/directives/carStatus/carStatus.html',
            controller: 'carStatusCtrl',
            replace: true
        }
    }

})();