
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

function invoke(fnName, args, callback) {
    jQuery.post(
        "/_internal_/fc",
        {
            fnName: fnName,
            args: JSON.stringify(args)
        },
        callback);
}

