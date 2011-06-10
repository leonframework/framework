
var person = (function() {

    var calls = 0;

    return {
        save: function(person) {

            if (session.clicks == null) {
                session.clicks = 0;
            }
            session.clicks = session.clicks + 1;
            java.lang.System.out.println(session.clicks);

            leon.browser("alert")("To Browser", "Got person [" + person.firstName + "]");

            return {
                calls: calls++,
                firstName: person.firstName,
                lastName: person.lastName
            };
        }
    };

})();
