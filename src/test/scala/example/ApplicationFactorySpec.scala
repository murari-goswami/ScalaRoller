package example

import org.scalatest._

class ApplicationFactorySpec extends FlatSpec with Matchers {
  "The ApplicationFactory object" should "say something" in {
    ApplicationFactory.greeting shouldEqual "Alles gut !!"
  }
}
