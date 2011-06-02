
var leon = (function() {

    var guice = Packages.com.google.inject;
    var Names = Packages.com.google.inject.name.Names;

    return {

        inject: function(clazz, name) {
            return injector.getInstance(guice.Key.get(clazz, Names.named(name)));
        },

        uplink: function(name) {
            var UplinkFunction = Packages.io.leon.web.comet.UplinkFunction;
            var uplinkFn = this.inject(UplinkFunction, name);
            return function() {
                var args = Array.prototype.slice.call(arguments);
                var json = JSON.stringify(args);
                uplinkFn.jsonApply(json);
            };
        }

    };

})();
