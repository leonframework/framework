
var leonJaxService = (function() {

    return {
        postMessage: function(user, room, message) {
            //browser.leon("alert")("To Browser", "Message: [" + message + "]");

            //leon.mongo.messages.insert(message);
            var now = new Date();

            var dateString = now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();

            return {
                message: dateString + "     " + user + " (" + room + ") : " + message
            };
        }
    };

})();
