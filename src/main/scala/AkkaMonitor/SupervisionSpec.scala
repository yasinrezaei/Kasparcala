package AkkaMonitor

import AkkaMonitor.SupervisionSpec.{FussyWordCounter, Report, Supervisor}
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class SupervisionSpec extends TestKit(ActorSystem("SupervisionSpec"))
with ImplicitSender
with AnyWordSpecLike
with BeforeAndAfterAll {
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
  "a supervisor " should {
    "resume its child" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      child ! "I love akka "
      child ! Report
      expectMsg(3)

      child ! "I love ......................................."

    }
  }

}
object SupervisionSpec{

  class Supervisor extends Actor {
    override val supervisorStrategy = OneForOneStrategy() {
      case _:NullPointerException => Restart
      case _:IllegalArgumentException => Stop
      case _:RuntimeException => Resume
      case _:Exception => Escalate
    }
    override def receive: Receive = {
      case props:Props =>
        val childRef = context.actorOf(props)
        sender() ! childRef
    }
  }




  case object Report
  class FussyWordCounter extends Actor{
    var words = 0
    override def receive: Receive = {
      case Report => sender() ! words
      case "" => throw new NullPointerException("sentence is empty")
      case sentence:String =>
        if (sentence.length > 20) throw new RuntimeException("sentence is too big")
        else if (!Character.isUpperCase(sentence(0))) throw new IllegalArgumentException("sentence must start with uper case")
        else words += sentence.split(" ").length
      case _ =>  throw new Exception("can only receive strings")
    }
  }

}
