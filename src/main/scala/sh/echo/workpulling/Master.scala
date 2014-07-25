package sh.echo.workpulling

import akka.actor._

class Master extends Actor {

  var workers = Set.empty[ActorRef]
  var workQueue = Vector.empty[Work]

  def receive = {

    case Register(worker) ⇒
      workers = workers + worker
      sender ! Registered(worker)

    case work: Work ⇒
      workQueue = workQueue :+ work
      workers foreach (_ ! WorkAvailable)

    case WorkRequest ⇒
      val (firstItem, rest) = workQueue.splitAt(1)
      firstItem.headOption match {
        case Some(work) ⇒ sender ! work
        case None       ⇒ sender ! NoWork
      }
      workQueue = rest

    case WorkRejected(work) ⇒
      workQueue = work +: workQueue
  }
}
