
var person = (function() {

    var calls = 0;

    var foo = "fake foo"
    var foo = leon.inject(java.lang.String, "foo");

    return {
        save: function(person) {
            calls++;

            println("JS server: " + person);
            println("JS server: " + person.address.zipcode);
            println("calls: " + calls)
            println("foo: " + foo)
            println("foo size: " + foo.length())

            var zc = person.address.zipcode;

            return {
                a: 1,
                b: zc * 3
            };
        }
    };

})();
