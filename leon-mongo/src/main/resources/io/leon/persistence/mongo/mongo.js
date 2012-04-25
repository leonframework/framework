
leon.mongo = (function() {

    var Mongo = Packages.io.leon.persistence.mongo

    return leon.inject(Mongo.ScriptableMongoDB)

})();
