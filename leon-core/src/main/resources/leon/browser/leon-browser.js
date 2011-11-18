
jQuery.fn.toObject = function() {
    return form2object(this.attr("id"));
};

var leon = (function() {

    return {

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

        displayErrorMessage: function(message) {
            $('<div></div>').html(message).activebar(
              {
                'button': '/leon/browser/images/activebar-closebtn.png',
                'icon': '/leon/browser/images/activebar-information.png'
              }
            );
        },

        alert: function(source, msg) {
            alert(source + ": " + msg);
        }

    };
})();

