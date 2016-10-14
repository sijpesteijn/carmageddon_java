(function () {
    'use strict';

    app.controller('panicButtonCtrl', panicButtonController).directive('panicButton', panicButtonDirective);

    panicButtonController.$inject = ['$rootScope', '$scope', '$resource'];

    function panicButtonController($rootScope, $scope, $resource) {
        $scope.throttleLimit = $rootScope.settings.throttleLimit;

        $scope.panic = function() {
            $resource('./rest/car/panic').save({}, {},
                function (success) {
                    console.debug('panic signal send', success);
                },
                function (error) {
                    console.error('panic signal failed. REALLY a big problem if there is still a lifeline', error);
                });
        }
    }

    function panicButtonDirective() {
        return {
            templateUrl: './scripts/directives/panicButton/panicButton.html',
            controller: 'panicButtonCtrl',
            replace: true
        }
    }

})();