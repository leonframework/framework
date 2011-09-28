leon.utils.createVar("io.leon.javascript.test.Tests");

io.leon.javascript.test.Tests = (function() {

  var service = TestService;

  return {
    testGetBean: function() {
      var bean = service.getTestBean();

      if(bean.x() != "x") throw "bean.x is " + bean.x + " but expected 'x'";
      if(bean.y() != 1) throw "bean.y is " + bean.x + " but expected '1'";
    },

    testSetBean: function() {
      service.setTestBean({
        "x": "Hello World",
        "y": 29322,
        "z": { "a": "abcdef", "b": 12389384 },
        "xs": ["a", "b", "c"]
      });
    },

    testMethodWithNumericArgs: function() {
      var x = service.methodWithNumericArgs(10, 10, 0, 1, 0.5, 1.5);
      if(x != 23) throw "exepcted 23 but got " + x;
    },

    testMethodWithString: function() {
      var x = service.methodWithString("Just call me Scala");
      if(x != "Hello World") throw "expected 'Hello World' but got: " + x;
    },

    testMethodWithJavaType: function() {
      var bean = service.getTestBean();
      service.setTestBean(bean);
    },

    testApplyMethodCall: function() {
      var data = {
        "x": "Hello World",
        "y": 29322,
        "z": { "a": "abcdef", "b": 12389384 }
      };

      service.setTestBean.apply(service, [data]);
    },

    methodWithJavaTestBean: function() {
      service.methodWithJavaTestBean({
        "x": "test",
        "y": 1,
        "z": { "x": "test" },
        "xs": ["a", "b", "c"]
      }).toJSON();
    },

    methodWithJavaList: function() {
      var bean = {
        "x": "test",
        "y": 1,
        "z": { "x": "test" }
      };

      var list = [bean, bean, bean];
      var result = service.methodWithJavaList(list).toJSON();

      if(result.length != list.length) throw "expected " + list.length + " but got " + result.length;

      for(var i = 0; i < result.length; i++)
        if(result[i].x != "test") throw "expected 'test' but got: " + result[i].x;
    },

    methodWithSeq: function() {
      var bean = {
        "x": "Hello World",
        "y": 1,
        "z": { "a": "abcdef", "b": 12389384 }
      };

      var list = [bean, bean, bean]
      var result = service.methodWithSeq(list).toJSON();

      if(result.length != list.length) throw "expected " + list.length + " but got " + result.length;

      for(var i = 0; i < result.length; i++)
        if(result[i].y != (bean.y + 1)) throw "expected " + (bean.y + 1) + " but got: " + result[i].y;
    },

    methodWithArray: function() {
      var result = service.methodWithArray([1, 2, 3]);
      if(result.length != 3) throw "expected 3 but got " + result.length;

      for(var i=0; i < result.length; i++)
        if(result[i] != (i + 2)) throw "expected " + (i + 2) + " but got " + result[i];
    },

    asJavaObject: function() {
      var bean = {
        "x": "test",
        "y": 1,
        "z": { "x": "test" }
      };

      bean.asJavaObject(Packages.io.leon.javascript.test.TestBean);
    }
  };
})();