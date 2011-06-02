
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

var leon = (function() {
    return {

        call: function(target, args, callback) {
            jQuery.post(
                "/leon/fc",
                {
                    target: target,
                    args: JSON.stringify(args)
                },
                callback);
        },

        alert: function(source, msg) {
            alert(source + ": " + msg);
        },

        registerUplink: function(pageId) {
            $.atmosphere.subscribe(
                "/leon/comet" + "?pageId=" + pageId + "&uplink=true",
                function(data) {
                    var message = JSON.parse(data.responseBody);
                    var target = eval(message.target);
                    target.apply(target, message.args);
                },
                $.atmosphere.request = {transport: "websocket"}
            );
        }

    };
})();
