(function () {
    'use strict';

    app.controller('autonomousCtrl', autonomousController).directive('autonomous', autonomousDirective);

    autonomousController.$inject = ['$scope', '$resource', 'websocketFactory', 'settingsFactory'];

    function autonomousController($scope, $resource, websocketFactory, settingsFactory) {
        $scope.msgs = [];
        $scope.racing = false;
        $scope.showSettings = false;
        $scope.settings = angular.undefined;

        var lastLookout = angular.undefined;
        var roi = document.getElementById("roi");
        var webcam = document.getElementById("webcam");
        var offset_x = 3;
        var offset_y = 40;
        var websocket;

        settingsFactory.getSettings().then(function (data) {
            $scope.settings = data;
            $scope.framerate = 1000/$scope.settings.delay;
        });

        $scope.startRace = function () {
            $resource('./rest/autonomous/start').save({}, {},
                function (success) {
                    $scope.msgs = [];
                    $scope.racing = true;
                },
                function (error) {
                    $scope.racing = false;
                    console.error('mode update failed', error);
                });
        };

        $scope.stopRace = function () {
            $resource('./rest/autonomous/stop').save({}, {},
                function (success) {
                    $scope.racing = false;
                },
                function (error) {
                    $scope.racing = false;
                    console.error('mode update failed', error);
                });
        };

        $scope.readyToRace = function() {
            if (lastLookout !== angular.undefined) {
                var status = lastLookout.status;
                if ( status ===  'READY_TO_RACE' || status ===  'RACE_STOPPED') {
                    $scope.racing = false;
                    return true;
                } else {
                    $scope.racing = true;
                }
            }
            return false;
        };

        $scope.$on('$destroy', function () {
            console.debug('destroying autonomous controller');
            websocket.closeConnection();
        });

        websocket = websocketFactory.create('autonomous/status');

        function setMousePosition(e) {
            var ev = e || window.event; //Moz || IE
            if (ev.pageX) { //Moz
                mouse.x = ev.pageX + window.pageXOffset;
                mouse.y = ev.pageY + window.pageYOffset;
            } else if (ev.clientX) { //IE
                mouse.x = ev.clientX + document.body.scrollLeft;
                mouse.y = ev.clientY + document.body.scrollTop;
            }
        }

        var mouse = {
            x: 0,
            y: 0,
            startX: 0,
            startY: 0
        };
        var element = null;

        roi.onmousemove = function (e) {
            setMousePosition(e);
            if (element !== null) {
                element.style.width = Math.abs(getMouseX() - mouse.startX) + 'px';
                element.style.height = Math.abs(getMouseY() - mouse.startY) + 'px';
                element.style.left = (getMouseX() - mouse.startX < 0) ? getMouseX() + 'px' : mouse.startX + 'px';
                element.style.top = (getMouseY() - mouse.startY < 0) ? getMouseY() + 'px' : mouse.startY + 'px';
            }
        };

        function getMouseX() {
            return mouse.x - offset_x;
        }

        function getMouseY() {
            return mouse.y - offset_y;
        }

        function getIntValue(str) {
            return str.substring(0, str.indexOf('p'));
        }

        roi.onclick = function (e) {
            if (element !== null) {
                var roiSettings = $scope.settings.trafficLightSettings.roi;
                roiSettings.x = getIntValue(element.style.left);
                roiSettings.y = getIntValue(element.style.top);
                roiSettings.width = getIntValue(element.style.width);
                roiSettings.height = getIntValue(element.style.height);
                $scope.delaySettingsUpdate();
                element = null;
                roi.style.cursor = "default";
            } else {
                while (roi.firstChild) {
                    roi.removeChild(roi.firstChild);
                }
                mouse.startX = getMouseX();
                mouse.startY = getMouseY();
                element = document.createElement('div');
                element.className = 'roi';
                element.style.left = getMouseX() + 'px';
                element.style.top = getMouseY() + 'px';
                roi.appendChild(element);
                roi.style.cursor = "crosshair";
            }
        };

        websocket.onMessage(function (message) {
            if (message.data !== 'pong') {
                if (message.data.indexOf('{') == 0) {
                    lastLookout = angular.fromJson(message.data);
                    $scope.readyToRace();
                    if ($scope.msgs.length > 0) {
                        var last = $scope.msgs[$scope.msgs.length - 1];
                        if (last.msg.indexOf(lastLookout.status) == 0) {
                            last.count++;
                        } else {
                            $scope.msgs.push({msg: lastLookout.status, count: 1});
                        }
                    } else {
                        $scope.msgs.push({msg: lastLookout.status, count: 1});
                    }
                } else {
                    webcam.src = 'data:image/png;base64,' + message.data;
                    webcam.width = $scope.settings.cameraDimension.width + 'px';
                    webcam.height = $scope.settings.cameraDimension.height + 'px';
                }
            }
        });
    }

    function autonomousDirective() {
        return {
            templateUrl: './scripts/directives/autonomous/autonomous.html',
            controller: 'autonomousCtrl',
            replace: true
        }
    }

})();