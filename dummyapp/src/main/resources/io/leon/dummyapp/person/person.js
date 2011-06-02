
var person = (function() {

    var uplinkAlert = leon.inject(Packages.io.leon.web.comet.UplinkFunction, "uplinkAlert");
    var testService = leon.inject(Packages.io.leon.dummyapp.person.TestService, "testService");

    return {
        save: function(person) {
            println("JS server: " + person);

            var zc = person.address.zipcode;

            //uplinkAlert.jsonApply('["person.js", "hello"]');
            testService.callUplinkAlert();

            return {
                a: 1,
                b: zc * 3
            };
        }
    };

})();
