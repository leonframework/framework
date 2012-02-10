package io.leon.persistence.sql.test

import org.specs2.mutable.Specification
import com.google.inject.{Guice, AbstractModule}
import java.sql.DriverManager
import io.leon.persistence.sql.{LeonSqlConfig, LeonSqlManager, LeonSqlModule}

class LeonSqlManagerSpec extends Specification {

  // Database

  Class.forName("org.h2.Driver")

  // Guice Module

  private val module = new AbstractModule {

    private val leonSqlConfig = new LeonSqlConfig
    leonSqlConfig.configFilePath = "/io/leon/persistence/sql/test/TestDatabase.yaml"
    leonSqlConfig.connectionFactory = () => {
      DriverManager.getConnection("jdbc:h2:mem:")
    }
    
    def configure() {
      install(new LeonSqlModule(leonSqlConfig))
    }
  }

  private def createManager(): LeonSqlManager = {
    val m = Guice.createInjector(module).getInstance(classOf[LeonSqlManager])
    m.executeCreateSchema()
    m.commit()
    m.executeCreateDdl()
    m.commit()
    m
  }

  // Test data

  private val personTestData = List(
    Map("id" -> 1, "firstName" -> "first1", "lastName" -> "last1"),
    Map("id" -> 2, "firstName" -> "first2", "lastName" -> "last2"),
    Map("id" -> 3, "firstName" -> "first3", "lastName" -> "last3"))

  // Tests

  "A LeonSqlManager" should {

    "create the person table" in {
      val m = createManager()
      val p = m.table("person")
      p.tableDef.name must_== "person"
      p.tableDef.sqlName must_== "PERSON"
      p.tableDef.sqlFullName must_== "LEONSQLTESTDATABASE.PERSON"
      p.tableDef.primaryKey must_== "id"
      p.tableDef.columns must  have size(3)
    }

  }

  "A Table" should {

    "insert data" in {
      val m = createManager()
      val p = m.table("person")
      val counts = p.insert(personTestData)
      counts must have size (3)
    }

    "Calculate the size" in {
      val m = createManager()
      val p = m.table("person")
      p.size must_== 0
      p.insert(personTestData)
      p.size must_== 3
    }

  }

}
