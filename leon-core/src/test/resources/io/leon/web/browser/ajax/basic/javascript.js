
function AjaxServiceCtrl(leon) {
    var self = this;
    this.result = "";

    this.method1 = function() {
        leon.service("/ajaxServiceJavaScript", "method1").call(function(result) {
            self.result = result;
        });
    }

}
