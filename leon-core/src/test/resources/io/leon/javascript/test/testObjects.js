leon.utils.createVar("io.leon.javascript.test.Tests");

io.leon.javascript.test.Tests = (function() {

  var service = TestService;

  return {
    testTestServiceGetTestBean: function() {
      var bean = service.getTestBean();

      if(bean.x != "x") throw "bean.x is " + bean.x + " but expected 'x'";
      if(bean.y != 1) throw "bean.y is " + bean.x + " but expected '1'";
      // if(bean.z.a != "a") throw "bean.z.a is " + bean.z.a + " but expected 'a'";

      bean.x = "Hello World";
      bean.y = 3;
      // bean.z.b = 7;

      service.setTestBean({
        "x": "Hello World",
        "y": 29322,
        "z": { "a": "abcdef", "b": 12389384 }
      });

      service.setInt(232);
    }
  };

})();