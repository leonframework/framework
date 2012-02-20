
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

var leon = (function() {

    return {

        deploymentMode: "development",

        service: function(url, methodName) {
            return {
                call: function() {
                    var args = Array.prototype.slice.call(arguments);

                    // check if last argument is a callback function
                    var params = [];
                    var callback = args[args.length - 1];
                    if (typeof callback === 'function') {
                        params = args.slice(0, args.length - 1);
                    } else {
                        params = args.slice(0, args.length);
                        callback = function() {};
                    }

                    var request = {
                        pageId: this.pageId,
                        member: methodName,
                        argsSize: params.length,
                        dataType: "json"
                    };

                    for(var i = 0; i < params.length; i++) {
                        request["arg" + i] = JSON.stringify(params[i]);
                    }

                    var handler = function(result) {
                        if (result != null && result.leonAjaxError) {
                            console.log("Server-side error while executing AJAX call. Check the console for more information.");
                            console.log(result);
                        } else {
                            callback(result);
                            if (!(typeof leon.angularDocument === 'undefined')) {
                                leon.angularDocument.$service("$updateView")();
                            }
                        }
                    }

                    jQuery.post(url, request, handler);
                }
            };
        },

        debug: function(msg) {
            if (leon.deploymentMode === "development") {
                console.log(msg);
            }
        },

        log: function(msg) {
            console.log(msg);
            if (!(typeof leon.cockpit === 'undefined')) {
                leon.cockpit.displayLogMessage(msg);
            }
        }

    };
})();

