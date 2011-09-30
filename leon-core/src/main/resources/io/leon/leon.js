
var leon = (function() {

    var guice = Packages.com.google.inject;
    var Names = Packages.com.google.inject.name.Names;

    Object.prototype.asJavaObject = function(clazz) {
        var converter = leon.inject(Packages.io.leon.javascript.Converter);
        return converter.jsToJava(this, clazz);
    }

    return {

        inject: function(clazz, name) {
            if (name === undefined) {
                return injector.getInstance(clazz);
            } else {
                return injector.getInstance(guice.Key.get(clazz, Names.named(name)));
            }
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
