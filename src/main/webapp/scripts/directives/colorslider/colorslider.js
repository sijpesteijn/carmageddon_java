(function () {
    'use strict';

    app.controller('colorsliderCtrl', colorsliderController).directive('colorSlider', colorsliderDirective);

    colorsliderController.$inject = ['$scope'];

    function colorsliderController($scope) {
        $scope.show = false;
        $scope.hue = 0;
        $scope.saturation = 0;
        $scope.value = 0;

        function init() {
            $scope.hue = $scope.rgb.hue;
            $scope.saturation = Math.round($scope.rgb.saturation/2.55);
            $scope.value = Math.round($scope.rgb.value/2.55);
        }

        $scope.result = { 'background-color' : 'hsl(' + $scope.hue + ',' + $scope.saturation + '%,' + $scope.value + '%)'};

        $scope.$watch('hue', function () {
            $scope.rgb.hue = $scope.hue;
            $scope.result = { 'background-color' : 'hsl(' + $scope.hue + ',' + $scope.saturation + '%,' + $scope.value + '%)'};
        });

        $scope.$watch('saturation', function () {
            $scope.rgb.saturation = $scope.saturation;
            $scope.result = { 'background-color' : 'hsl(' + $scope.hue + ',' + $scope.saturation + '%,' + $scope.value + '%)'};
        });

        $scope.$watch('value', function () {
            $scope.rgb.value = $scope.value;
            $scope.result = { 'background-color' : 'hsl(' + $scope.hue + ',' + $scope.saturation + '%,' + $scope.value + '%)'};
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