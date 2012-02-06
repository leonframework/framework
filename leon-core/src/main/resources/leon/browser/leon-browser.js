
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

var leon = (function() {

    var ajaxCallsCount = 0;

    return {

        deploymentMode: "development",

        getAjaxCallsCount: function() {
            return ajaxCallsCount;
        },

        call: function(target, args, callback) {

            var params = {
                pageId: this.pageId,
                target: target,
                argsSize: args.length,
                dataType: "json"
            };

            for(var i = 0; i < args.length; i++) {
                params["arg" + i] = JSON.stringify(args[i]);
            }

            var handler = function(result) {
                if (result.leonAjaxError) {
                    console.log("Server-side error while executing AJAX call. Check the console for more information.");
                    console.log(result);
                } else {
                    callback(result);
                }
                ajaxCallsCount = ajaxCallsCount + 1;
            }

            jQuery.post(
                leon.contextPath + "/leon/ajax",
                params,
                handler);
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

