(function () {
    'use strict';

    app.controller('touchpadCtrl', touchpadController).directive('touchpad', touchpadDirective);

    touchpadController.$inject = ['$scope', '$resource', '$interval', 'settingsFactory'];

    function touchpadController($scope, $resource, $interval, settingsFactory) {
        $scope.show = false;
        $scope.settings = 0;
        $scope.throttle = 0;
        $scope.angle = 0;

        settingsFactory.getSettings().then(function (data) {
            $scope.settings = data;
        });

        var joystick = new VirtualJoystick({
            container: document.getElementById('touchpad'),
            baseX: 400,
            baseY: 400,
            mouseSupport: true,
            limitStickTravel: true,
            stickRadius: 150
        });

        function postThrottle(throttle) {
            $resource('./rest/car/engine/:throttle').save({
                    throttle: throttle
                }, {},
                function (success) {
                    // console.debug('throttle send', success);
                },
                function (error) {
                    console.error('throttle update failed', error);
                });
        }

        $interval(function () {
            var currAngle = Math.round(joystick.deltaX()/3);
            if (currAngle != $scope.angle) {
                $resource('./rest/car/steer/:angle').save({
                        angle: currAngle
                    }, {},
                    function (success) {
                        // console.debug('angle send', success);
                    },
                    function (error) {
                        console.error('angle update failed', error);
                    });
                $scope.angle = currAngle;
            }
            var currThrottle = -1 * Math.round(joystick.deltaY()/3);
            if ($scope.settings.maxThrottle > 0 && currThrottle > $scope.settings.maxThrottle) {
                currThrottle = $scope.settings.maxThrottle;
                if ($scope.throttle != currThrottle) {
                    $scope.throttle = currThrottle;
                    postThrottle(currThrottle);
                }
            } else if ($scope.settings.maxThrottle > 0 && currThrottle < -$scope.settings.maxThrottle) {
                currThrottle = -$scope.settings.maxThrottle;
                if ($scope.throttle != currThrottle) {
                    $scope.throttle = currThrottle;
                    postThrottle(currThrottle)
                }
            } else {
                if (currThrottle != $scope.throttle) {
                    postThrottle(currThrottle);
                    $scope.throttle = currThrottle;
                }
            }
        }, 1 / 30 * 1000);

        $scope.updateMaxThrottle = function () {
            settingsFactory.updateSettings($scope.settings);
        }
    }

    function touchpadDirective() {
        return {
            templateUrl: './scripts/directives/touchpad/touchpad.html',
            controller: 'touchpadCtrl',
            replace: true
        }
    }

})();