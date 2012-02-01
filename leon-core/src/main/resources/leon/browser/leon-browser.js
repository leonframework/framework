
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
                callback(result);
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

        alert: function(source, msg) {
            alert(source + ": " + msg);
        }

    };
})();

