(function() {
    'use strict';

    app.controller('lifelineCtrl', lifelineController).directive('lifeline', lifelineDirective);

    lifelineController.$inject = ['$scope', '$timeout', 'websocketFactory'];

    function lifelineController($scope, timeout, websocketFactory) {
        $scope.heartbeat = false;

        var websocket = websocketFactory.create('lifeline');
        sendPing();

        function sendPing() {
            $timeout(function () {
                websocket.sendMessage('ping');
            }, 500);
        }

        websocket.onMessage(function(message) {
            $scope.heartbeat = !$scope.heartbeat;
            sendPing();
        });

        $scope.$on('$destroy', function () {
            console.debug('destroying lifeline controller');
            websocket.closeConnection();
        });

    }

    function lifelineDirective() {
        return {
            templateUrl: './scripts/directives/lifeline/lifeline.html',
            controller: 'lifelineCtrl',
            replace: true
        }
    }

})();