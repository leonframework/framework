function ReverserServiceCtrl() {

    var self = this;

    this.text = "Hello World!";
    this.reversed = "";
    this.toUpperCase = false;

    this.reverse = function() {
        leon.service("/reverserService", "reverse").call(this.text, this.toUpperCase, function(result) {
            self.reversed = result;
        });
    }
}