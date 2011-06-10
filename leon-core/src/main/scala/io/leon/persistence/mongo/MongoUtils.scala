package io.leon.persistence.mongo

import io.leon.javascript.{LeonScriptEngine, RhinoUtils}
import com.mongodb._

import collection._
import scala.collection.JavaConverters
import sjson.json.Serializer.SJSON
import dispatch.json._
import org.bson.types.ObjectId

private[mongo] object MongoUtils {

  type RawMap = Map[_,_]

  var engine: LeonScriptEngine = _

  implicit def mapToDbObject(data: Map[_,_]): DBObject = {
    import JavaConverters._

    val map = data collect {
      case (k, v:Map[_,_]) => (k -> mapToDbObject(v))
      case x => x
    }

    new BasicDBObject(map.asJava)
  }

  implicit def dbObjectToMap(dbObject: DBObject): mutable.Map[_,_] = {
    import JavaConverters._

    dbObject.toMap.asScala collect {
      case (k, v:DBObject) => (k -> dbObjectToMap(v))
      case x => x
    }
  }

  def rhinoObjectToAny(obj: AnyRef): Any = {

    def jsValueToAny(js: JsValue): Any = js match {
      case obj:JsObject =>
        obj.self.map { x =>
          (x._1.self -> jsValueToAny(x._2))
        }
      case obj:JsString => obj.self
      case obj:JsNumber => obj.self
      case x => x.toString
    }

    // TODO use Scriptable/ NativeObject directly
    val json = RhinoUtils.rhinoObject2Json(engine, obj)
    val jsObj = SJSON.in(json).asInstanceOf[JsObject]

    jsValueToAny(jsObj)
  }

  def anyToRhinoObject(obj: Any) = {

    def anyToJsValue(obj: Any): JsValue = obj match {
      case m:Map[_,_] =>
        val jsMap = m map { x => (JsString(x._1) -> anyToJsValue(x._2)) }
        JsObject(jsMap.toSeq)

      case seq:Seq[_] => JsValue(seq map { anyToJsValue })
      case oid: ObjectId => JsValue(oid.toString)
      case x => JsValue(x)
    }

    // TODO use Scriptable/ NativeObject directly
    val js = anyToJsValue(obj)
    RhinoUtils.json2RhinoObject(engine, js.toString)
  }
}





