
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

var leon = (function() {

    return {

        call: function(target, args, callback) {
            jQuery.post(
                "/leon/ajax",
                {
                    pageId: this.pageId,
                    target: target,
                    args: JSON.stringify(args),
                    dataType: "json"
                },
                callback);
        },

        alert: function(source, msg) {
            alert(source + ": " + msg);
        }

    };
})();

