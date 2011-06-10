
leon.mongo = (function(dbName) {

    var LeonMongoManagerClass = Packages.io.leon.persistence.mongo.LeonMongoManager;
    var MongoUtils = Packages.io.leon.persistence.mongo.MongoUtils

    var mManager = leon.inject(LeonMongoManagerClass);
    var db = mManager.getDb(dbName);

    return function(collName) {
        return MongoCollection(db.getCollection(collName));
    }

    // ----- wrapper functions -----

    function MongoCollection(coll) {

        return {
            find: function(q) {
                var list = coll.find(MongoUtils.rhinoObjectToAny(q));
                return MongoUtils.anyToRhinoObject(list);
            },

            insert: function(obj) {
                var map = MongoUtils.rhinoObjectToAny(obj)
                coll.insert(map);
            }
        }
    }

});
