
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

function invoke(fnName, args, callback) {
    jQuery.post(
        "/leon/fc",
        {
            fnName: fnName,
            args: JSON.stringify(args)
        },
        callback);
}

