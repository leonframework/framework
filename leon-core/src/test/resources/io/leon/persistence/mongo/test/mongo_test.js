
test = (function() {

    function createPerson() {
        return {
            firstName: "Firstname",
            lastName: "Lastname",
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
            if(result.length != 1)
                throw ("expected one result for person with _id" + person._id + " but got " + result.length);

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
            if(result.length != 1)
                throw ("expected one result for person but got " + result.length);

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
            if(result.length != 2)
                throw ("expected one result for person but got " + result.length);

            if(spec_test.count() != result.length)
                throw ("spec_test.count() is " + spec_test.count() + " but expected " + result.length);

            if(result[0].address.zipCode != "88888")
                throw "expected '88888' but got " + result[0].address.zipCode;

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
        }
    }
})();
