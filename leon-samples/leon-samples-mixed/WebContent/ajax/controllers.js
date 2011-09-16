function PersonCtrl($xhr) {
  self = this;

  this.countries = null;
  this.states = [];
  this.hobbyToAdd = "";

  personService("getCountries")(function(result) {
    self.countries = result;
    self.$service('$updateView')();
  });

  this.person = {
    firstName: "John",
    lastName: "Doe",
    address: {
      zipcode: "73728",
      country: { isoCode: "DE", name: "Germany" },
      city: "Esslingen"
    },
    hobbies: []
  };

  this.addHobby = function() {
    var hobby = self.hobbyToAdd;
    if (hobby != "") {
      self.person.hobbies.push(hobby);
      self.hobbyToAdd = "";
    }
  };

  this.savePerson = function() {
    person("save")(5, self.person, function(result) {
      console.log("ajax call result firstname: " + result.firstName);
    });
  };

  this.doSomething = function() {
    personService("doSomething")(self.person, function(result) {
      console.log("result of doSomething: " + result);
    });
  };
}