(function() {
    'use strict';

    app.controller('carStatusCtrl', carStatusController).directive('carStatus', carStatusDirective);

    carStatusController.$inject = ['$rootScope', '$scope','$resource', 'websocketFactory'];

    function carStatusController($rootScope, $scope, $resource, websocketFactory) {
        $scope.connected = false;
        $scope.car;
        $scope.modes = ['disabled','manual','autonomous'];

        var websocket = websocketFactory.create('car/status');

        websocket.onMessage(function (message) {
            if (message.data !== 'pong') {
                console.log('car status: ' + message.data);
                $scope.car = angular.fromJson(message.data);
                $rootScope.settings.throttleLimit = $scope.car.engine.throttleLimit;
                $rootScope.carMode = $scope.car.mode;
            }
        });

        $scope.updateCarMode = function() {
            $resource('./rest/car/mode/:mode').save({
                    mode: $rootScope.carMode
                }, {},
                function (success) {
                    // console.debug('mode send', success);
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        };

        $scope.$on('$destroy', function () {
            console.debug('destroying controller');
            websocket.close();
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