
var leon = (function() {

    return {
        inject: function(clazz, name) {
            return injector.getInstance(
                Packages.com.google.inject.Key.get(clazz, Packages.com.google.inject.name.Names.named(name)));
        }
    };

})();
