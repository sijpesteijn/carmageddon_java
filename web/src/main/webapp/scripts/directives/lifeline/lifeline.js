(function() {
    'use strict';

    app.controller('lifelineCtrl', lifelineController).directive('lifeline', lifelineDirective);

    lifelineController.$inject = ['$scope', 'websocketFactory'];

    function lifelineController($scope, websocketFactory) {
        $scope.heartbeat = false;
        var websocket = websocketFactory.create('lifeline');

        websocket.onMessage(function(message) {
            $scope.heartbeat = !$scope.heartbeat;
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