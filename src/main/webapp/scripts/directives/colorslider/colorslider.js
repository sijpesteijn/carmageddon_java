(function () {
    'use strict';

    app.controller('colorsliderCtrl', colorsliderController).directive('colorSlider', colorsliderDirective);

    colorsliderController.$inject = ['$scope'];

    function colorsliderController($scope) {
        $scope.show = false;
        $scope.red = 0;
        $scope.green = 0;
        $scope.blue = 0;

        function init() {
            $scope.red = $scope.rgb.red;
            $scope.green = $scope.rgb.green;
            $scope.blue = $scope.rgb.blue;
        }

        $scope.result = { 'background-color' : 'rgb(' + $scope.red + ',' + $scope.green + ',' + $scope.blue + ')'};

        $scope.$watch('red', function () {
            $scope.rgb.red = $scope.red;
            $scope.result = { 'background-color' : 'rgb(' + $scope.red + ',' + $scope.green + ',' + $scope.blue + ')'};
        });

        $scope.$watch('green', function () {
            $scope.rgb.green = $scope.green;
            $scope.result = { 'background-color' : 'rgb(' + $scope.red + ',' + $scope.green + ',' + $scope.blue + ')'};
        });

        $scope.$watch('blue', function () {
            $scope.rgb.blue = $scope.blue;
            $scope.result = { 'background-color' : 'rgb(' + $scope.red + ',' + $scope.green + ',' + $scope.blue + ')'};
        });

        init();
    }

    function colorsliderDirective() {
        return {
            templateUrl: './scripts/directives/colorslider/colorslider.html',
            controller: 'colorsliderCtrl',
            replace: true,
            scope: {
                rgb: '='
            }
        }
    }

})();