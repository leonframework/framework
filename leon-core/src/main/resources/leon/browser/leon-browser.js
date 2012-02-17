
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

var leon = (function() {

    return {

        deploymentMode: "development",

        service: function(url) {
            return {
                call: function() {
                    // convert arguments to array
                    var args = Array.prototype.slice.call(arguments);
                    var methodName = args[0];

                    // check if last argument is a callback function
                    var params = [];
                    var callback = args[args.length - 1];
                    if (typeof callback === 'function') {
                        params = args.slice(1, args.length - 1);
                    } else {
                        params = args.slice(1, args.length);
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

        // TODO delete
        //alert: function(source, msg) {
        //    alert(source + ": " + msg);
        //},

        displayMessageBox: function(div) {
            alert("foobar");
        }

    };
})();

