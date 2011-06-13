
// Set top-level global var to be independent from browser or server side

if (this.window === undefined) {
    // Server
    global = this;
} else {
    // Browser
    global = window;
}

// Util functions

leon.utils = (function() {
    return {


        createVar: function(name) {
            var names = name.split(".");
            var root = global;
            while (names.length > 0) {
                var first = names.shift();
                if (root[first] === undefined) {
                    root[first] = new Object;
                }
                root = root[first];
            }
        }
    };
})();
