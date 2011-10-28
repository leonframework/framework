package io.leon.web.browser

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import java.io.BufferedWriter
import com.google.inject.{TypeLiteral, Injector, Inject}
import com.google.inject.name.Named
import io.leon.web.ajax.AjaxHandler

class VirtualLeonJsFile @Inject()(injector: Injector) extends HttpServlet {

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    import scala.collection.JavaConverters._

    res.setContentType("text/javascript")
    val out = new BufferedWriter(res.getWriter)

    val serverObjects = injector.findBindingsByType(new TypeLiteral[AjaxHandler]() {})
    serverObjects.asScala foreach { o =>
      val browserName = o.getKey.getAnnotation.asInstanceOf[Named].value()
      out.write(createJavaScriptFunctionDeclaration(browserName))
    }
    out.close()
  }

  private def createJavaScriptFunctionDeclaration(name: String): String = {
    """
    leon.utils.createVar("server");
    leon.utils.createVar("server.%s");
    server.%s = function (methodName) {
      return function() {
        // convert arguments to array
        var args = Array.prototype.slice.call(arguments);

        // check if last argument is the callback function
        var callback = args[args.length - 1];
        if (typeof callback === 'function') {
          var params = args.slice(0, args.length - 1);
          leon.call("%s." + methodName, params, callback);
        } else {
          leon.call("%s." + methodName, args, function() {});
        }
      };
    }
    """.format(name, name, name, name)
  }

}

