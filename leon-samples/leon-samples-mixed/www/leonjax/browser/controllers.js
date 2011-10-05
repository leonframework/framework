function LeonJaxCtrl($xhr) {
  self = this;

  //messages array is displayed in the html via angular Databinding
  this.messages = [];
  this.rooms = [{name: "Rom"}, {name:"Sydney"}];
  this.user = {
    name: "Jan",
    room: ""
  };

  this.postMessage = function(message) {
    server.leonJaxService("postMessage")(this.user.name, this.user.room.name, message, function(result) {
      console.log(result);
      self.messages.unshift(result.message);
      self.message = "";
    });
  };

}
