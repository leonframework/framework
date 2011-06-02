
var guice = Packages.com.google.inject;
var Names = Packages.com.google.inject.name.Names;

var leon = (function() {

    return {
        inject: function(clazz, name) {
            return injector.getInstance(guice.Key.get(clazz, Names.named(name)));
        }
    };

})();
