
var leonJaxService = (function() {

    return {
        postMessage: function(user, room, message) {
          leon.publishMessage("messages", { "room": room }, message);
        }
    };

})();
