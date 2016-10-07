(function () {
    'use strict';

    app.controller('settingsCtrl', settingsController).directive('settings', settingsDirective);

    settingsController.$inject = ['$scope', 'settingsFactory'];

    function settingsController($scope, settingsFactory) {
        $scope.framerate;
        $scope.settings;
        $scope.tab = 'general';
        $scope.subtab = 'colors';

        settingsFactory.getSettings().then(function (data) {
            $scope.settings = data;
            $scope.framerate = 1000/$scope.settings.delay;
        });

        // $scope.$watch('settings', function () {
        //     $scope.delaySettingsUpdate();
        // }, true);

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

        $scope.updateFramerate = function (framerate) {
            $scope.framerate = framerate;
            $scope.settings.delay = 1000/$scope.framerate;
            settingsFactory.updateSettings($scope.settings);
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