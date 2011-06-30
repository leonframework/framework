leon.utils.createVar("io.leon.javascript.test.Tests");

io.leon.javascript.test.Tests = (function() {

  var service = TestService;

  return {
    testGetTestBean: function() {
      var bean = service.getTestBean();

      if(bean.x != "x") throw "bean.x is " + bean.x + " but expected 'x'";
      if(bean.y != 1) throw "bean.y is " + bean.x + " but expected '1'";

      bean.x = "Hello World";
      bean.y = 3;
      bean.z.b = 7;
    },

    testSetTestBean: function() {
      service.setTestBean({
        "x": "Hello World",
        "y": 29322,
        "z": { "a": "abcdef", "b": 12389384 }
      });
    },

    testMethodWithDefaultArgs: function() {
      var bean = service.getTestBean();
      service.methodWithDefaultArgs(bean);
    },

    testMethodWithNumericArgs: function() {
      var x = service.methodWithNumericArgs(10, 10, 0, 1, 0.5, 1.5)
      if(x != 23) throw "exepcted 23 but got " + x;
    }
  };

})();