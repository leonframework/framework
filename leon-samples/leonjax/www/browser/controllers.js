function LeonJaxCtrl($xhr) {

  self = this;

  //messages array is displayed in the html via angular Databinding
  this.messages = [];
  this.rooms = [{name: "Rom"}, {name:"Sydney"}];
  this.user = {
    name: "Jan",
    room: ""
  };

  this.$watch("user.room", "updateFilter()");

  this.updateFilter = function() {
    if(this.user.room)
      leon.comet.updateFilter("messages", "room", this.user.room.name);
  };

  this.postMessage = function(message) {
    server.leonJaxService("postMessage")(this.user.name, this.user.room.name, message);
  };

}
