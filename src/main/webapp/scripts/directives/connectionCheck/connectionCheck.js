(function() {
    'use strict';

    app.controller('connectionCheckCtrl', connectionCheckController).directive('connectionCheck', connectionCheckDirective);

    connectionCheckController.$inject = ['$scope', 'connectionCheckWebSocket', '$interval'];

    function connectionCheckController($scope, connectionCheckWebSocket, $interval) {

        $scope.lifeline = false;
        connectionCheckWebSocket.onMessage(function(message) {
            $scope.lifeline = !$scope.lifeline;
        });

        connectionCheckWebSocket.onError(function(error) {
            $scope.lifeline = false;
        });

        $interval(function () {
            connectionCheckWebSocket.sendMessage('ping');
        }, 500)
    }

    function connectionCheckDirective() {
        return {
            templateUrl: './scripts/directives/connectionCheck/connectionCheck.html',
            controller: 'connectionCheckCtrl',
            replace: true
        }
    }

})();