function ReverserServiceCtrl() {

    var self = this;

    this.text = "Hello World!";
    this.reversed = "";
    this.toUpperCase = false;

    this.reverse = function() {
        server.reverserService("reverse")(this.text, this.toUpperCase, function(result) {
            self.reversed = result;
            self.$service('$updateView')();
        });
    }
}