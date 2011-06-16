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

            var arr = result.toArray();
            if(arr.length != result.size() - 1) // size - 'first fetch'
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
                java.lang.System.out.println("Firstname: " + p.firstName);
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
                java.lang.System.out.println(names[i]);
            }

            return true;
        },

        regex_find: function() {
            var spec_test = leon.mongo.spect_test;
            spec_test.drop();

            spec_test.insert(createPerson());

            var result = spec_test.find({ "firstName": /^First.*$/g });
            if(result.size() != 1)
                throw "expected one but got " + result.size();

            return true;
        }

    }
})();
