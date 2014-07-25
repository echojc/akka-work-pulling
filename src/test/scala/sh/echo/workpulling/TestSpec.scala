package sh.echo.workpulling

import akka.actor._
import akka.testkit._
import org.scalatest._

abstract class TestSpec(_system: ActorSystem)
    extends TestKit(_system)
    with FunSpecLike
    with ShouldMatchers
    with BeforeAndAfter
    with BeforeAndAfterAll {

  def this() = this(ActorSystem())
}
