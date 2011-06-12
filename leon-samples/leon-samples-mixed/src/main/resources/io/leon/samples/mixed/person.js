
var person = (function() {

    var calls = 0;
    return {
        save: function(no, person) {
            if (session.clicks == null) {
                session.clicks = 0;
            }
            session.clicks = session.clicks + 1;
            java.lang.System.out.println(session.clicks);

            leon.browser("alert")("To Browser", "Got person no." + no +  "[" + person.firstName + "]");

            leon.mongo.people.insert(person);

            return {
                calls: calls++,

                _id: person._id,
                firstName: person.firstName,
                lastName: person.lastName
            };
        }
    };

})();
