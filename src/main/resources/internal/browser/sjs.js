
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

function invoke(fnName, args, callback) {
    jQuery.post(
        "/sjs/_sjs/fc",
        {
            fnName: fnName,
            args: JSON.stringify(args)
        },
        callback);
}

function receive(queueName, callback) {
    $.atmosphere.subscribe(
        "/atmosphere/queue/" + queueName + "?name=Roman",
        function(a) { console.log("ATMO"+ a.responseBody) },
        $.atmosphere.request = {transport: "autodetect"});
}




