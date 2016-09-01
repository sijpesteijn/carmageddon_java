(function () {
    'use strict';

    app.controller('touchpadCtrl', touchpadController).directive('touchpad', touchpadDirective);

    touchpadController.$inject = ['$rootScope', '$scope', '$resource', '$interval'];

    function touchpadController($rootScope, $scope, $resource, $interval) {
        $scope.show = false;
        $scope.throttleLimit = $rootScope.settings.throttleLimit;
        $scope.throttle = 0;
        $scope.angle = 0;

        $rootScope.$watch('settings.throttleLimit', function () {
            $scope.throttleLimit = $rootScope.settings.throttleLimit;
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
            var currAngle = Math.round(joystick.deltaX());
            if (currAngle != $scope.angle) {
                $resource('./rest/car/steer/:angle').save({
                        angle: $scope.angle
                    }, {},
                    function (success) {
                        // console.debug('angle send', success);
                    },
                    function (error) {
                        console.error('angle update failed', error);
                    });
                $scope.angle = currAngle;
            }
            var currThrottle = Math.round(joystick.deltaY());
            if ($scope.throttleLimit > 0 && currThrottle > $scope.throttleLimit) {
                currThrottle = $scope.throttleLimit;
                if ($scope.throttle != currThrottle) {
                    $scope.throttle = currThrottle;
                    postThrottle(currThrottle);
                }
            } else if ($scope.throttleLimit > 0 && currThrottle < -$scope.throttleLimit) {
                currThrottle = -$scope.throttleLimit;
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

        joystick.addEventListener('touchstart', onTouchStart, false);
        joystick.addEventListener('touchend', onTouchEnd, false);
        joystick.addEventListener('mousemove', onTouchStart, false);
        joystick.addEventListener('mouseup', onTouchEnd, false);

        function onTouchStart() {
            console.log('Start');
        }

        function onTouchEnd() {
            console.log('End');
        }

        $scope.stop = function() {
            $resource('./rest/car/stop').save({}, {},
                function (success) {
                    console.debug('stop send', success);
                },
                function (error) {
                    console.error('stop update failed', error);
                });
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