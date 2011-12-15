function ReverserServiceCtrl() {

    var self = this;

    this.text = "Hello World!";
    this.reversed = "";
    this.toUpperCase = false;

    this.reverse = function() {
        var request = {
            text: this.text,
            toUpperCase: this.toUpperCase
        }

        server.reverserService("reverse")(request, function(result) {
            self.reversed = result.reversed;
            self.$service('$updateView')();
        });
    }
}