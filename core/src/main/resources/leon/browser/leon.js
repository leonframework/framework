
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

function invoke(target, args, callback) {
    jQuery.post(
        "/leon/fc",
        {
            target: target,
            args: JSON.stringify(args)
        },
        callback);
}

