
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

var leon = (function() {

    return {

        deploymentMode: "development",

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

            jQuery.post(
                leon.contextPath + "/leon/ajax",
                params,
                callback);
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

