package io.leon.persistence.mongodb

import java.util.logging.Logger
import com.mongodb._

import collection._
import scala.collection.JavaConverters
import javax.management.remote.rmi._RMIConnection_Stub

private[mongodb] object MongoUtils {

  def mapToDbObject(map: Map[_,_]): DBObject = {
    import JavaConverters._
    new BasicDBObject(map.asJava)
  }

  def dbObjectToMap(dbObject: DBObject): mutable.Map[_,_] = {
    import JavaConverters._
    dbObject.toMap.asScala
  }
}





