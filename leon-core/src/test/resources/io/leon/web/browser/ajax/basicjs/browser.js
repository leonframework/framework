
function AjaxServiceCtrl() {
    var self = this;
    this.result = "";

    this.method1 = function() {
        leon.service("/ajaxService").call("method1", function(result) {
            self.result = result;
            self.$service('$updateView')();
        });
    }

}
