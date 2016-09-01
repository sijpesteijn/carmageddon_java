(function() {
    'use strict';

    app.controller('connectionCheckCtrl', connectionCheckController).directive('connectionCheck', connectionCheckDirective);

    connectionCheckController.$inject = ['$scope', '$websocket', '$interval', '$timeout', '$location'];

    function connectionCheckController($scope, $websocket, $interval, $timeout, $location) {
        var connection = $websocket($location.absUrl().replace(/http/g,'ws') + 'check');

        $scope.lifeline = false;
        $scope.tries = 0;
        $scope.connected = false;

        var pinger;

        function startPinger()  {
            pinger = $interval(function () {
                connection.send('ping');
            }, 500)
        }

        function stopPinger() {
            $interval.cancel(pinger);
        }

        function reconnect() {
            while(!$scope.connected && $scope.tries++ < 5) {
                $timeout(function () {
                    connection = $websocket($location.absUrl().replace(/http/g,'ws') + 'check');
                }, 1000);
            }
        }

        connection.onOpen(function () {
            $scope.tries = 0;
            $scope.lifeline = true;
            $scope.connected = true;
            startPinger();
        });

        connection.onMessage(function(message) {
            $scope.lifeline = !$scope.lifeline;
        });

        connection.onError(function(error) {
            $scope.lifeline = false;
            $scope.connected = false;
            stopPinger();
            reconnect();
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