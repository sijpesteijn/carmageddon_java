(function() {
    'use strict';

    app.controller('connectionCheckCtrl', connectionCheckController).directive('connectionCheck', connectionCheckDirective);

    connectionCheckController.$inject = ['$scope', 'websocketFactory'];

    function connectionCheckController($scope, websocketFactory) {
        var websocket = websocketFactory.create('check');
        $scope.lifeline = false;

        websocket.onMessage(function(message) {
            $scope.lifeline = !$scope.lifeline;
        });

    }

    function connectionCheckDirective() {
        return {
            templateUrl: './scripts/directives/connectionCheck/connectionCheck.html',
            controller: 'connectionCheckCtrl',
            replace: true
        }
    }

})();