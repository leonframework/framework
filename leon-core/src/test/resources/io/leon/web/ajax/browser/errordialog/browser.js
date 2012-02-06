function AjaxServiceCtrl() {
    var self = this;
    this.result = "";
    this.withoutError = function() {
        server.ajaxService("call")(false, function(result) {
            self.result = result;
            self.$service('$updateView')();
        });
    }
    this.withError = function() {
        server.ajaxService("call")(true, function(result) {
            self.result = result;
            self.$service('$updateView')();
        });
    }
}
