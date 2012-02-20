
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
                            leon.log("Ajax [" + url + " " + methodName + "] ERROR");
                            console.log(result);
                        } else {
                            callback(result);
                            if (!(typeof leon.angularDocument === 'undefined')) {
                                leon.angularDocument.$service("$updateView")();
                            }
                            leon.log("Ajax [" + url + " " + methodName + "] done");
                        }
                    }

                    jQuery.post(url, request, handler);
                    leon.log("Ajax [" + url + " " + methodName + "] called");
                }
            };
        },

        hasCockpit: function() {
            return !(typeof leon.cockpit === 'undefined');
        },

        log: function(msg) {
            if (leon.deploymentMode === "development") {
	            console.log(msg);
	            if (leon.hasCockpit()) {
	                leon.cockpit.displayLogMessage(msg);
	            }
            }
        }

    };
})();

