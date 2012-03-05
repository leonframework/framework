
function AjaxServiceCtrl(leon) {
    var self = this;
    this.result = "";

    this.method1 = function() {
        leon.service("/ajaxServiceJava", "method1").call(function(result) {
            self.result = result;
        });
    }

}
