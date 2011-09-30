function LeonJaxCtrl($xhr) {
  self = this;

  //messages array is displayed in the html via angular Databinding
  this.messages = [];

  this.postMessage = function(message) {
    server.leonJaxService("postMessage")(message, function(result) {
      console.log(result);
      self.messages.push(message);
      self.message = "";
    });
  };

}


var cometMessages = {
  newMessage: function(message) {
  
  }
}