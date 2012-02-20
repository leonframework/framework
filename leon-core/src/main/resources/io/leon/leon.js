
var leon = (function() {

    var LoggerFactory = Packages.org.slf4j.LoggerFactory;
    var guice = Packages.com.google.inject;
    var Names = Packages.com.google.inject.name.Names;

    Object.prototype.properties = function() {
      var result = [];
      for (var property in this) {
        if (this.hasOwnProperty(property))
          result.push(property);
      }
      return result;
    };

    return {

        getLogger: function(name) {
            return LoggerFactory.getLogger("JS: " + name);
        },

        getInjector: function() {
            return injector;
        },

        inject: function(clazz, name) {
            if (name === undefined) {
                return injector.getInstance(clazz);
            } else {
                return injector.getInstance(guice.Key.get(clazz, Names.named(name)));
            }
        },

        getGson: function() {
            return leon.inject(Packages.com.google.gson.Gson);
        },

        publishMessage: function(topic, filter, data) {
            var cometRegistry = leon.inject(Packages.io.leon.web.comet.CometRegistry);

            var filterMap = new Packages.java.util.HashMap();
            filter.properties().forEach(function(key) {
                filterMap.put(key, filter[key]);
            });

            var datastring = leon.getGson().toJson(data);
            cometRegistry.publish(topic, filterMap, datastring);
            return true;
        },

        parseLess: function(lessString) {
          var result;
          var parser = new less.Parser();

          parser.parse(lessString, function (e, root) {
            if (e) {
               throw(e);
            } else {
                result =  root.toCSS();
            }
          });

          return result;
        }
    };

})();
