
var leon = (function() {

    var LoggerFactory = Packages.org.slf4j.LoggerFactory;
    var guice = Packages.com.google.inject;
    var Names = Packages.com.google.inject.name.Names;

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

        sendTopicMessage: function(topic, data, filter) {
            var topicsService = leon.inject(Packages.io.leon.web.TopicsService);

			if (filter) {
                topicsService.send(topic, data, filter);
			} else {
				topicsService.send(topic, data);
			}

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
