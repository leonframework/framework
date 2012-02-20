function AjaxServiceCtrl() {
    var self = this;
    this.result = "";

    this.withoutError = function() {
        leon.service("/ajaxService", "call").call(false, function(result) {
            self.result = result;
            self.$service('$updateView')();
        });
    }

    this.withError = function() {
        leon.service("/ajaxService", "call").call(true, function(result) {
            self.$service('$updateView')();
        });
    }
}
