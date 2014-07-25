package sh.echo.workpulling

import akka.actor._
import akka.testkit._

class MasterTest extends TestSpec {

  it("accepts registrations from workers and replies with an acknowledgement") {
    new Fixture {
      testActor.send(master, Register(testActor.ref))
      testActor.expectMsg(Registered(testActor.ref))
    }
  }

  it("broadcasts a work available message to all registered workers on receiving work") {
    new Fixture {
      val workers = List.fill(3)(TestProbe())
      workers foreach (worker ⇒ master ! Register(worker.ref))

      master ! Work(())
      workers foreach (_.expectMsg(WorkAvailable))
    }
  }

  describe("handing out work") {
    it("gives work on request") {
      new Fixture {
        val work = Work(1)
        master ! work

        testActor.send(master, WorkRequest)
        testActor.expectMsg(work)
      }
    }

    it("gives out work it receives in order") {
      new Fixture {
        val work = (1 to 3) map (Work)
        work foreach (master ! _)

        work foreach { work ⇒
          testActor.send(master, WorkRequest)
          testActor.expectMsg(work)
        }
      }
    }

    it("replies that no work is available when that is the case") {
      new Fixture {
        testActor.send(master, WorkRequest)
        testActor.expectMsg(NoWork)
      }
    }

    it("allows work to be rejected which is then redistributed as soon as possible") {
      new Fixture {
        val work = (1 to 3) map (Work)
        work foreach (master ! _)
        val List(work1, work2, work3) = work.toList

        testActor.send(master, WorkRequest)
        testActor.expectMsg(work1)

        testActor.send(master, WorkRequest)
        testActor.expectMsg(work2)
        testActor.send(master, WorkRejected(work2))

        testActor.send(master, WorkRequest)
        testActor.expectMsg(work2)

        testActor.send(master, WorkRequest)
        testActor.expectMsg(work3)
      }
    }
  }

  trait Fixture {
    val testActor = TestProbe()
    val master = system.actorOf(Props(classOf[Master]))
  }
}
