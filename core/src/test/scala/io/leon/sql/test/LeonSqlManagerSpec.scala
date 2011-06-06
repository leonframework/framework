package io.leon.sql.test

import org.specs2.mutable.Specification
import com.google.inject.{Guice, AbstractModule}
import java.sql.DriverManager
import org.specs2.execute.Result
import io.leon.sql.{LeonSqlConfig, LeonSqlManager, LeonSqlModule}

class LeonSqlManagerSpec extends Specification {

  // Database
  Class.forName("org.h2.Driver")

  // Guice Module
  private val module = new AbstractModule {

    private val leonSqlConfig = new LeonSqlConfig
    leonSqlConfig.configFilePath = "/io/leon/sql/test/TestDatabase.yaml"
    leonSqlConfig.connectionFactory = () => {
      DriverManager.getConnection("jdbc:h2:mem:")
    }
    
    def configure() {
      install(new LeonSqlModule(leonSqlConfig))
    }
  }

  private def withDb(code: LeonSqlManager => Result): Result = {
    val m = Guice.createInjector(module).getInstance(classOf[LeonSqlManager])
    m.executeCreateSchema()
    m.commit()

    m.executeCreateDdl()
    m.commit()

    val result = code(m)
    m.commit()

    m.executeDropSchema()
    m.commit()
    m.close()
    result
  }

  // Tests

  "A LeonSqlManagerFactory" should {

    "create the person table" in {
      withDb { m =>
        val p = m.table("person")
        p.name must_== "person"
        p._sqlName must_== "PERSON"
        p.primaryKey must_== "id"
        p.columns must  have size(3)
      }
    }

    "insert data into the person table" in {
      withDb { m =>
        val p = m.table("person")
        val testData = Map(
          "id" -> 1,
          "firstName" -> "Max",
          "lastName" -> "Mustermann"
        )
        p.insert(testData)

        1 must_== (1)
      }
    }


  }

}
