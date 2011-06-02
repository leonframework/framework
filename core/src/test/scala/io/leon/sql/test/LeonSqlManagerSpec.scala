package io.leon.sql.test

import org.specs2.mutable.Specification
import com.google.inject.{Guice, AbstractModule}
import io.leon.sql.{LeonSqlConfig, LeonSqlManager, LeonSqlModule}
import java.sql.DriverManager

class LeonSqlManagerSpec extends Specification {

  // Database
  Class.forName("org.h2.Driver")

  // LeonSqlConfig

  private val leonSqlConfig = new LeonSqlConfig

  leonSqlConfig.configFilePath = "/io/leon/sql/test/TestDatabase.yaml"
  leonSqlConfig.connectionFactory = () => { DriverManager.getConnection("jdbc:h2:~/h2testdatabase") }

  // Guice Module
  private val module = new AbstractModule {
    def configure() {
      install(new LeonSqlModule(leonSqlConfig))
    }
  }

  private val injector = Guice.createInjector(module)

  private def createManager(): LeonSqlManager = {
    val m = injector.getInstance(classOf[LeonSqlManager])
    m.executeDropDdl()
    m.close()
    injector.getInstance(classOf[LeonSqlManager])
  }

  // Tests

  "A LeonSqlManagerFactory" should {

    "read all tables" in {
      val m = createManager()
      m.close()

      m.tables must have size(2)
    }

    "insert data" in {
      val m = createManager()

      val personTable = m.tables.filter(_.name == "person").head
      personTable.insert(Map(
        "id" -> 1,
        "firstName" -> "Roman",
        "lastName" -> "Roelofsen"
      ))

      m.close()

      1 must_== (1)
    }

  }

}
