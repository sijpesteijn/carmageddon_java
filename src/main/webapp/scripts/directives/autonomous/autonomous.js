(function () {
    'use strict';

    app.controller('autonomousCtrl', autonomousController).directive('autonomous', autonomousDirective);

    autonomousController.$inject = ['$scope', '$resource', '$timeout', 'websocketFactory'];

    function autonomousController($scope, $resource, $timeout, websocketFactory) {
        var websocket;
        $scope.lowerHSVMin;
        $scope.lowerHSVMax;
        $scope.upperHSVMin;
        $scope.upperHSVMax;

        $scope.framerate;
        $scope.settings;

        $scope.msgs = [];
        $scope.racing = false;
        $scope.showSettings = false;
        $scope.tab = 'general';
        $scope.subtab = 'colors';
        var updateTimeout = angular.undefined;
        var lastLookout = angular.undefined;
        var roi = document.getElementById("roi");
        var webcam = document.getElementById("webcam");
        var offset_x = 3;
        var offset_y = 40;

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

        $scope.$watch('settings', function () {
            $scope.delaySettingsUpdate();
        }, true);


        function getSettings() {

            function buildHsv(hsv) {
                return 'hsv('+ hsv.hue + ',' + Math.round(hsv.saturation / (255/100)) + '%,' + Math.round(hsv.brightness / (255/100)) + '%)';
            }

            $resource('./rest/autonomous/settings').get({}, {},
                function (settings) {
                    $scope.lowerHSVMin = buildHsv(settings.trafficLightSettings.lowerHSVMin);
                    $scope.lowerHSVMax = buildHsv(settings.trafficLightSettings.lowerHSVMax);
                    $scope.upperHSVMin = buildHsv(settings.trafficLightSettings.upperHSVMin);
                    $scope.upperHSVMax = buildHsv(settings.trafficLightSettings.upperHSVMax);
                    $scope.settings = settings;
                    $scope.framerate = 1000/$scope.settings.delay;
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        }

        function updateSettings() {

            function getHsv(hsv) {
                var splits = hsv.split(',');
                var result = {
                    hue: splits[0].split('(')[1],
                    saturation: Math.round(splits[1].substring(0,splits[1].length-1)*(255/100)),
                    brightness: Math.round(splits[2].substring(0,splits[2].length-2)*(255/100))
                };
                return result;
            }

            var settings = $scope.settings;
            settings.trafficLightSettings.lowerHSVMin = getHsv($scope.lowerHSVMin);
            settings.trafficLightSettings.lowerHSVMax = getHsv($scope.lowerHSVMax);
            settings.trafficLightSettings.upperHSVMin = getHsv($scope.upperHSVMin);
            settings.trafficLightSettings.upperHSVMax = getHsv($scope.upperHSVMax);
            $resource('./rest/autonomous/settings').save({},
                settings,
                function (success) {
                },
                function (error) {
                    console.error('mode update failed', error);
                });
        }

        $scope.options = {
            format: 'hsv',
            hue: true,
            swatch: true
        };

        $scope.eventApi = {
            onChange: function(api, color, $event) {
                var id = api.getElement().attr('id');
                if (id === 'lowerHSVMin') {
                    $scope.lowerHSVMin = color;
                } else if (id === 'lowerHSVMax') {
                    $scope.lowerHSVMax = color;
                } else if (id === 'upperHSVMin') {
                    $scope.upperHSVMin = color;
                } else {
                    $scope.upperHSVMax = color;
                }
                $scope.delaySettingsUpdate();
            }
        };

        $scope.delaySettingsUpdate = function () {
            if (updateTimeout != null) {
                $timeout.cancel(updateTimeout);
            }
            updateTimeout = $timeout(updateSettings, 500);
        };

        $scope.updateFramerate = function (framerate) {
            $scope.framerate = framerate;
            $scope.settings.delay = 1000/$scope.framerate;
            $scope.delaySettingsUpdate();
        };

        $scope.$on('$destroy', function () {
            console.debug('destroying autonomous controller');
            websocket.closeConnection();
        });

        websocket = websocketFactory.create('autonomous/status');
        getSettings();

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