(function () {
    'use strict';

    app.controller('streamCtrl', streamController).directive('stream', streamDirective);

    streamController.$inject = [];

    function streamController() {
    }

    function streamDirective() {
        return {
            templateUrl: './scripts/directives/stream/stream.html',
            controller: 'streamCtrl',
            replace: true
        }
    }

})();