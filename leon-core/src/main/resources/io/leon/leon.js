
var leon = (function() {

    var guice = Packages.com.google.inject;
    var Names = Packages.com.google.inject.name.Names;

    return {

        inject: function(clazz, name) {
            return injector.getInstance(guice.Key.get(clazz, Names.named(name)));
        },

        getBrowserObject: function(name) {
            var BrowserObject = Packages.io.leon.web.comet.BrowserObject;
            var ref = this.inject(BrowserObject, name);
            return function(methodName) {
                return function() {
                    var args = Array.prototype.slice.call(arguments);
                    var json = JSON.stringify(args);
                    ref.jsonApply(methodName, json);
                };
            };
        }

    };

})();
