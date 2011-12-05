
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

var leon = (function() {

    return {

        deploymentMode: "development",

        call: function(target, args, callback) {
            jQuery.post(
                leon.contextPath + "/leon/ajax",
                {
                    pageId: this.pageId,
                    target: target,
                    args: JSON.stringify(args),
                    dataType: "json"
                },
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

