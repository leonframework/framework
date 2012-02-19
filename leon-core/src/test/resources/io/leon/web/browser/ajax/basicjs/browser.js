
function AjaxServiceCtrl() {
    var self = this;
    this.result = "";

    this.method1 = function() {
        leon.service("/ajaxService", "method1").call(function(result) {
            self.result = result;
            self.$service('$updateView')();
        });
    }

}
