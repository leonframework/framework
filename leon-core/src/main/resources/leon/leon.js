
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

var leon = (function() {
    return {

        call: function(target, args, callback) {
            jQuery.post(
                "/leon/ajax",
                {
                    target: target,
                    args: JSON.stringify(args),
                    dataType: "json"
                },
                callback);
        },

        alert: function(source, msg) {
            alert(source + ": " + msg);
        },

        handleBrowserObjectMethodCall: function(objectName, methodName, args) {
            var obj = eval(objectName);
            var method = obj[methodName];
            method.apply(method, args);
        },

        registerPage: function(pageId) {
            var handle = this.handleBrowserObjectMethodCall;
            $.atmosphere.subscribe(
                "/leon/registerPage" + "?pageId=" + pageId + "&uplink=true",
                function(data) {
                    var message = JSON.parse(data.responseBody);
                    if (message.type === "browserObjectMethodCall") {
                        handle(message.object, message.method, message.args);
                    }
                },
                $.atmosphere.request = {transport: "websocket"}
            );
        }

    };
})();
