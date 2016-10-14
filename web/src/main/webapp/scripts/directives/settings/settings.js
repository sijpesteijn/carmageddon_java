(function () {
    'use strict';

    app.controller('settingsCtrl', settingsController).directive('settings', settingsDirective);

    settingsController.$inject = ['$scope', 'settingsFactory'];

    function settingsController($scope, settingsFactory) {
        $scope.framerate;
        $scope.settings;
        $scope.tab = 'general';
        $scope.subtab = 'colors';
        $scope.trafficLightViewTypes = [
            {id:'result',name:'Result'},
            {id:'hsv',name:'HSV'},
            {id:'baw',name:'Black & White'}
        ];
        $scope.roadViewTypes = [
            {id:'result',name:'Result'},
            {id:'baw',name:'Black & White'},
            {id:'canny',name:'Canny'}
        ];
        $scope.lowerHSVMin = 'hsv(0, 100%, 100%)';
        $scope.lowerHSVMax = 'hsv(0, 100%, 100%)';
        $scope.upperHSVMin = 'hsv(0, 100%, 100%)';
        $scope.upperHSVMax = 'hsv(0, 100%, 100%)';

        function buildHsv(hsv) {
            return 'hsv('+ hsv.hue + ',' + Math.round(hsv.saturation / (255/100)) + '%,' + Math.round(hsv.brightness / (255/100)) + '%)';
        }

        settingsFactory.getSettings().then(function (data) {
            $scope.settings = data;
            $scope.lowerHSVMin = buildHsv(data.trafficLightSettings.lowerHSVMin);
            $scope.lowerHSVMax = buildHsv(data.trafficLightSettings.lowerHSVMax);
            $scope.upperHSVMin = buildHsv(data.trafficLightSettings.upperHSVMin);
            $scope.upperHSVMax = buildHsv(data.trafficLightSettings.upperHSVMax);
            $scope.framerate = Math.round(1000/$scope.settings.delay);
        });

        $scope.$watch('settings', function () {
            $scope.updateSettings();
        }, true);

        $scope.updateSettings = function () {
            settingsFactory.updateSettings($scope.settings);
        };

        $scope.options = {
            format: 'hsv',
            hue: true,
            swatch: true
        };

        function getHsv(hsv) {
            var splits = hsv.split(',');
            var result = {
                hue: Math.round(splits[0].split('(')[1]),
                saturation: Math.round(splits[1].substring(0,splits[1].length-1)*(255/100)),
                brightness: Math.round(splits[2].substring(0,splits[2].length-2)*(255/100))
            };
            return result;
        }

        $scope.eventApi = {
            onChange: function(api, color, $event) {
                var id = api.getElement().attr('id');
                var hsv = getHsv(color);
                if (id === 'lowerHSVMin') {
                    $scope.settings.trafficLightSettings.lowerHSVMin = hsv;
                } else if (id === 'lowerHSVMax') {
                    $scope.settings.trafficLightSettings.lowerHSVMax = hsv;
                } else if (id === 'upperHSVMin') {
                    $scope.settings.trafficLightSettings.upperHSVMin = hsv;
                } else {
                    $scope.settings.trafficLightSettings.upperHSVMax = hsv;
                }
                $scope.updateSettings();
            }
        };

        $scope.updateFramerate = function (framerate) {
            $scope.framerate = framerate;
            $scope.settings.delay = 1000/$scope.framerate;
            settingsFactory.updateSettings();
        };
    }

    function settingsDirective() {
        return {
            templateUrl: './scripts/directives/settings/settings.html',
            controller: 'settingsCtrl',
            replace: true
        }
    }

})();