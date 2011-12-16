
var ReverserService = (function() {

    var StringBuffer = Packages.java.lang.StringBuffer;

    return {
        reverse: function(text, toUpperCase) {
            var sb = new StringBuffer(text);
            var reversed = sb.reverse().toString();
            if (toUpperCase) {
                return reversed.toUpperCase();
            } else {
                return reversed;
            }
        }
    };
})();

