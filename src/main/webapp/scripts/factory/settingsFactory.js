(function() {
        'use strict';

        app.factory('settings', function () {

            var settings = {
                throttleLimit: 20
            };


            return {
                get : function () {
                    return settings;
                },
                set : function (newSettings) {
                    settings = newSettings
                }
            }
        })
    }
)();