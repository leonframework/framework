
var leonJaxService = (function() {

    var calls = 0;
    //var personService = leon.inject(Packages.io.leon.samples.mixed.PersonService);

    return {
        postMessage: function(message) {

            if (session.clicks == null) {
                session.clicks = 0;
            }
            session.clicks = session.clicks + 1;
            java.lang.System.out.println("clicks: " + session.clicks);

            leon.browser("alert")("To Browser", "Message no: " + no +  "[" + message + "]");

            //leon.mongo.messages.insert(message);

            return {
                message: message + " wurde gepostet"
            };
        }
    };

})();
