(function () {
    'use strict';

    app.controller('settingsCtrl', settingsController).directive('settings', settingsDirective);

    settingsController.$inject = ['$scope', 'settingsFactory'];

    function settingsController($scope, settingsFactory) {
        $scope.framerate;
        $scope.settings;
        $scope.tab = 'general';
        $scope.subtab = 'lane';
        $scope.startLanes = ['left','right'];


        settingsFactory.getSettings().then(function (data) {
            $scope.settings = data;
            $scope.framerate = Math.round(1000/$scope.settings.delay);
        });

        $scope.$watch('settings', function () {
            $scope.updateSettings();
        }, true);

        $scope.updateSettings = function () {
            settingsFactory.updateSettings($scope.settings);
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