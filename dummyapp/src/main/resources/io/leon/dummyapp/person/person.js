
var person = (function() {

    var uplinkAlert = leon.uplink("uplinkAlert");

    return {
        save: function(person) {
            uplinkAlert("person server code", "Got person [" + person.firstName + "]");
            return {
                firstName: person.firstName,
                lastName: person.lastName
            };
        }
    };

})();
