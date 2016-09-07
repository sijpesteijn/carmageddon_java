(function() {
    'use strict';

    app.controller('lifelineCtrl', lifelineController).directive('lifeline', lifelineDirective);

    lifelineController.$inject = ['$scope', 'websocketFactory'];

    function lifelineController($scope, websocketFactory) {
        var websocket = websocketFactory.create('lifeline');
        $scope.heartbeat = false;

        websocket.onMessage(function(message) {
            $scope.heartbeat = !$scope.heartbeat;
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