(function() {
   'use strict';

    app.controller('settingsCtrl', settingsController).directive('settings', settingsDirective);

    settingsController.$inject = ['$rootScope', '$scope'];

    function settingsController($rootScope, $scope) {
        $scope.show = false;
        $scope.settings = $rootScope.settings;

        $scope.save = function() {
            $rootScope.settings = $scope.settings;
            $scope.show = false;
        };
    }



    function settingsDirective() {
        return {
            templateUrl : './scripts/directives/settings/settings.html',
            controller: 'settingsCtrl',
            replace: true
        }
    }

})();