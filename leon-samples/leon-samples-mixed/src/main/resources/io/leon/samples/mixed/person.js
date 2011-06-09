
var person = (function() {

    var calls = 0;

    return {
        save: function(person) {

            leon.browser("alert")("To Browser", "Got person [" + person.firstName + "]");

            return {
                calls: calls++,
                firstName: person.firstName,
                lastName: person.lastName
            };
        }
    };

})();
