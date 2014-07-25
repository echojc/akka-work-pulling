package sh.echo

import akka.actor._

package object workpulling {

  case class Register(worker: ActorRef)
  case class Registered(worker: ActorRef)

  case class Work(work: Any)
  case object WorkAvailable
  case object WorkRequest
  case object NoWork
  case class WorkRejected(work: Work)
}
