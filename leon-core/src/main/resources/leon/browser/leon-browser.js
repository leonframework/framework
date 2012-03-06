
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

var _leon = (function() {

    return {

        deploymentMode: "development",

        service: function(url, methodName, onComplete) {
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
                            getLeon().log("Ajax [" + url + " " + methodName + "] ERROR");
                            console.log(result);
                        } else {
                            callback(result);
                            getLeon().log("Ajax [" + url + " " + methodName + "] done");
                        }
                        if (!(typeof onComplete === "undefined")) {
                            onComplete()
                        }
                    }

                    jQuery.post(url, request, handler);
                    getLeon().log("Ajax [" + url + " " + methodName + "] called");
                }
            };
        },

        hasCockpit: function() {
            return !(typeof getLeon().cockpit === 'undefined');
        },

        log: function(msg) {
            if (getLeon().deploymentMode === "development") {
	            console.log(msg);
	            if (getLeon().hasCockpit()) {
	                getLeon().cockpit.displayLogMessage(msg);
	            }
            }
        }

    };
})();

var getLeon = function() {
	return _leon;
}

