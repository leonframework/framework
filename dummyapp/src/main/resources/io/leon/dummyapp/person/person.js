
var person = (function() {

    var uplinkAlert = leon.uplink("uplinkAlert");

    return {
        save: function(person) {
            println("JS server: " + person);

            var zc = person.address.zipcode;

            uplinkAlert("person.js", "hello2", 123456);

            return {
                a: 1,
                b: zc * 3
            };
        }
    };

})();
