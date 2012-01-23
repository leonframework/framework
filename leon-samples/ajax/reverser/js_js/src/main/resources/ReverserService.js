
var ReverserService = (function() {

    var logger = leon.getLogger("ReverserService");

    var StringBuffer = Packages.java.lang.StringBuffer;

    return {
        reverse: function(text, toUpperCase) {

            logger.info("#####" + JSON.stringify({"a": new Packages.java.lang.String("bla")}) + "#####");

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

