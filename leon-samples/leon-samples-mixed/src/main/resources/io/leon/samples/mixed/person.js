
var person = (function() {

    var calls = 0;
    return {
        save: function(person) {
            var people = leon.mongo("leon")("people");

            leon.browser("alert")("To Browser", "Got person [" + person.firstName + "]");

            people.insert(person);

            var res = people.find({"firstName": "John"});
            println(res[0].lastName);

            return {
                calls: calls++,

                firstName: person.firstName,
                lastName: person.lastName
            };
        }
    };

})();
