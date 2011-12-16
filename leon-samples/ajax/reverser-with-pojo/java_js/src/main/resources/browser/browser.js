function ReverserServiceCtrl() {

    var self = this;

    this.text1 = "Hello Java!";
    this.reversed1 = "";
    this.toUpperCase1 = false;

    this.text2 = "Hello JavaScript!";
    this.reversed2 = "";
    this.toUpperCase2 = false;

    this.text3 = "Hello CoffeeScript!";
    this.reversed3 = "";
    this.toUpperCase3 = false;

    this.reverse = function() {
        var word1 = {
            text: this.text1,
            toUpperCase: this.toUpperCase1
        };
        var word2 = {
            text: this.text2,
            toUpperCase: this.toUpperCase2
        };
        var word3 = {
            text: this.text3,
            toUpperCase: this.toUpperCase3
        };

        var request = {
            words: [word1, word2, word3]
        }

        server.reverserService("reverse")(request, function(result) {
            self.reversed1 = result[0];
            self.reversed2 = result[1];
            self.reversed3 = result[2];

            self.$service('$updateView')();
        });
    }
}
