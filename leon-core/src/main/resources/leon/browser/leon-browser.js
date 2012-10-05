
var _leon = (function() {

    return {

        deploymentMode: "production",

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
                        } else {
                            callback(result);
                            getLeon().log("Ajax [" + url + " " + methodName + "] done");
                        }
                        if (!(typeof onComplete === "undefined")) {
                            onComplete()
                        }
                    };

                    jQuery.post(getLeon().contextPath + url, request, handler);
                    getLeon().log("Ajax [" + url + " " + methodName + "] called");
                }
            };
        },

        subscribeTopic: function(topicId, handler) {
            return getLeon().comet.subscribeTopic(topicId, handler);
        },

        filterTopic: function(topicId, key, value) {
            return getLeon().comet.updateFilter(topicId, key, value);
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
};

