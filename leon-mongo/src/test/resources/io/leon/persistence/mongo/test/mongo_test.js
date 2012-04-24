leon.utils.createVar("io.leon.persistence.mongo.test");

io.leon.persistence.mongo.test = (function() {

    function createPerson(i) {
        if(!i) i = "";

        return {
            firstName: "Firstname" + i,
            lastName: "Lastname" + i,
            address: {
                zipCode: "88888",
                city: "Esslingen"
            }
        };
    }

    return {
        insert: function() {
            leon.mongo.setWriteConcern({ "w": 1, "fsync": true });

            var spec_test = leon.mongo.spec_test;
            spec_test.drop();

            var person = createPerson();

            spec_test.insert(person);

            if(!person._id)
                throw "person._id doesn't exist after insert";


            var result = spec_test.find({"_id": person._id});
            if(result.length() != 1)
                throw ("expected one result for person with _id" + person._id + " but got " + result.length());

            return true;
        },

        save: function() {
            var spec_test = leon.mongo.spec_test;
            spec_test.drop();

            var person = createPerson();

            spec_test.save(person);
            if(!person._id)
                throw "person._id doesn't exist after insert";

            person.firstname = "John";
            spec_test.save(person);

            var result = spec_test.find();
            if(result.length() != 1)
                throw ("expected one result for person but got " + result.length());

            if(result[0].firstname != "John")
                throw "expected John for firstname but got: " + result[0].firstname;

            return true;
        },

        find: function() {
            var spec_test = leon.mongo.spec_test;
            spec_test.drop();

            spec_test.insert(createPerson());
            spec_test.insert(createPerson());

            var result = spec_test.find();
            if(result.length() != 2)
                throw ("expected one result for person but got " + result.length());

            if(spec_test.count() != result.length())
                throw ("spec_test.count() is " + spec_test.count() + " but expected " + result.length());

            if(result[0].address.zipCode != "88888")
                throw "expected '88888' but got " + result[0].address.zipCode;

            return true;
        },

        findOne: function() {
            var spec_test = leon.mongo.spec_test;
            spec_test.drop();

            spec_test.insert(createPerson());

            var result1 = spec_test.findOne({"address.zipCode": "88888"});
            if(!result1)
                throw "expected single result but got null";

            var noResult = spec_test.findOne({"x": "doesnotexists"});
            if(noResult)
                throw "null expected but got: " + noResult;

            return true;
        },

        remove: function() {
            var spec_test = leon.mongo.spec_test;
            spec_test.drop();

            spec_test.insert(createPerson());
            spec_test.insert(createPerson());

            spec_test.remove({"firstName": "Firstname"})
            if(spec_test.count() != 0)
                throw "expected zero but got: " + spec_test.count() + " for spec_test.count()";

            return true;
        },

        cursor: function() {
            var spec_test = leon.mongo.spect_test;
            spec_test.drop();

            for(var i=0; i < 20; i++) {
                spec_test.insert(createPerson(i));
            }

            var result = spec_test.find().skip(10).limit(5);

            if(!result.hasNext())
                throw "hasNext() return false but expected true";

            var person = result.next();
            if(person.firstName != "Firstname10")
                throw "expected Firstname10 but got " + person.firstName;

            result = spec_test.find().skip(10).limit(5);
            var arr = result.toArray();
            if(arr.length != result.size())
                throw "unexpected size of array - expected " + (result.size() - 1) + " but was " + arr.length;

            if(result.size() != 5)
                throw "expected five for result.size() but got: " + result.size();

            if(result.count() != 20)
                throw "expected 20 for result.count() but got: " + result.count();

            result.close();

            return true;
        },

        sort: function() {
            var spec_test = leon.mongo.spect_test;
            spec_test.drop();

            spec_test.insert({"x": 3});
            spec_test.insert({"x": 1});
            spec_test.insert({"x": 5});
            spec_test.insert({"x": 4});
            spec_test.insert({"x": 2});

            var result = spec_test.find().sort({"x": "1"});

            for(var i=0; i < 5; i++) {
             if(result[i].x != (i+1))
                throw "expected x=" + (i+1) + " but got " + result[i].x;
            }

            return true;
        },

        cursor_forEach: function() {
            var spec_test = leon.mongo.spect_test;
            spec_test.drop();

            for(var i=0; i < 20; i++) {
                spec_test.insert(createPerson(i));
            }

            var i = 0;
            spec_test.find().forEach(function(p) {
                var x = p.firstname;
                i++;
            });

            if(i != spec_test.count())
                throw "expected " + spec_test.count() + " iterations but was " + i;

            return true;
        },

        cursor_map: function() {
            var spec_test = leon.mongo.spect_test;
            spec_test.drop();

            for(var i=0; i < 20; i++) {
                spec_test.insert(createPerson(i));
            }

            var names = spec_test.find().map(function(p) {
                return p.firstName + " " + p.lastName;
            });

            if(names.length != spec_test.count())
                throw "expected " + spec_test.count() + " iterations but was " + i;

            for(var i=0; i < names.length; i++) {
                var x= names[i];
            }

            return true;
        },

        regex_find: function() {
            var spec_test = leon.mongo.spec_test;
            spec_test.drop();

            spec_test.insert(createPerson());

            var result = spec_test.find({ "firstName": /^first.*$/i });
            if(result.size() != 1)
                throw "expected one but got " + result.size();

            return true;
        },

        arrays: function() {
          var spec_test = leon.mongo.spec_test;
          spec_test.drop();

          var people = [];
          for(var i=0; i < 10; i++) {
            people[i] = createPerson(i);
          }

          spec_test.insert({ "people": people });
          var result = spec_test.findOne();

          if(result.people.length != 10)
            throw "expected '5' but got " + result.people.length;

          if(result.people[0].firstName != "Firstname")
            throw "expected 'Firstname' but got " + result.people[0].firstName;

          return true;
        },

        mapReduce: function() {
          var spec_test = leon.mongo.spec_test;
          spec_test.drop();

          var people = [];
          for(var i=0; i < 10; i++) {
            people[i] = createPerson(i);
            spec_test.insert(people[i]);
          }

          var map = function() {
            emit(this.address.zipCode, {count: 1})
          }

          var reduce = function(key, values) {
            var count = 0;
            for(var i=0; i < values.length; i++) {
              count += values[i]["count"];
            }

            return {count: count};
          }

          var output = spec_test.mapReduce(map, reduce, {out: "mapReduceTest" });
          if(output.results().length != 1)
            throw "expected '1' but got " + results.length;

          if(output.results()[0].value.count != 10)
            throw "expected '10' but got " + results[0].value.count;

          output.drop();

          return true;
        },

        getStats: function() {
          var stats = leon.mongo.getStats();
          return stats.ok();
        },

        setWriteConcern: function() {
           leon.mongo.setWriteConcern();
           leon.mongo.setWriteConcern({ "w": 1 });
           leon.mongo.setWriteConcern({ "w": 1, "wtimeout": 0 });
           leon.mongo.setWriteConcern({ "w": 1, "wtimeout": 10, "fsync": false });
           leon.mongo.setWriteConcern({ "w": 2, "wtimeout": 500, "fsync": true, "j": true });

           return true;
        }
    }
})();
