function LeonJaxCtrl($xhr) {
  self = this;
  this.messages = [];

  this.postMessage = function(message) {
    leonJaxService("postMessage")(message, function(result) {
      console.log(result);
      self.messages.push(message);
      self.message = "";
    });
  };

}


var cometMessages = {
  newMessage: function(message)
}