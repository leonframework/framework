
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

