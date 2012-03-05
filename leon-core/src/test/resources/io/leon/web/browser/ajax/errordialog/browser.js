
function AjaxServiceCtrl(leon) {
    var self = this;
    this.result = "";

    this.withoutError = function() {
        leon.service("/ajaxService", "call").call(false, function(result) {
            self.result = result;
        });
    }

    this.withError = function() {
        leon.service("/ajaxService", "call").call(true, function(result) {
        });
    }
}
