
var person = (function() {

    return {
        save: function(person) {
            leon.browser.all("alert")("person server code", "Got person [" + person.firstName + "]");
            return {
                firstName: person.firstName,
                lastName: person.lastName
            };
        }
    };

})();
