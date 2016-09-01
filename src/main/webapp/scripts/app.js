'use strict';

var app = angular.module('carmageddon', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngRoute',
    'ngWebSocket'
]);

app.run(function ($rootScope) {
    $rootScope.settings = {
        throttleLimit: 20
    }
});
