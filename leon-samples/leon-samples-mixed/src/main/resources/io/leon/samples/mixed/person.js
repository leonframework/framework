
var person = (function() {

    return {
        save: function(person) {

            leon.browser("alert")("To Browser", "Got person [" + person.firstName + "]");

            return {
                firstName: person.firstName,
                lastName: person.lastName
            };
        }
    };

})();
