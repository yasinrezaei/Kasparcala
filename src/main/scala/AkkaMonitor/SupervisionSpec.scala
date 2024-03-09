package AkkaMonitor

import AkkaMonitor.SupervisionSpec.{AllForOneSupervisor, FussyWordCounter, NoDeathOnRestartSupervisor, Report, Supervisor}
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, AllForOneStrategy, OneForOneStrategy, Props, Terminated}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import org.glassfish.jersey.message.internal.HttpHeaderReader.Event
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

      child ! "I love ...... ......... ........... ............."
      child ! Report
      expectMsg(3)


    }

    "restart its child in case of an empty sentence" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      child ! "I love akka "
      child ! Report
      expectMsg(3)

      child ! ""
      child ! Report
      expectMsg(0)
    }

    "terminate its child in case of major error" in {
      val supervisor = system.actorOf(Props[Supervisor],"supervisor")
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      watch(child)
      child ! "akka is good"
      val terminatedmsg = expectMsgType[Terminated]
      assert(terminatedmsg.actor == child)
    }

    "escalate an error when it doesnt know what to do" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      watch(child)
      child ! 43
      val terminatedmsg = expectMsgType[Terminated]
      assert(terminatedmsg.actor == child)

    }

    "A kinder supervisor" should {
      "not kill children in case its restarted or escalate" in {
        val supervisor = system.actorOf(Props[NoDeathOnRestartSupervisor],"NoDeathSupervisor")
        supervisor ! Props[FussyWordCounter]
        val child = expectMsgType[ActorRef]

        child ! "Akka is cool"
        child ! Report
        expectMsg(3)

        child ! 44
        child ! Report
        expectMsg(0)

      }
    }

    "An all-for-one supervisor " should {

      "apply the all-for-one strategy" in {
        val supervisor = system.actorOf(Props[AllForOneSupervisor],"allforonestartegy")
        supervisor ! Props[FussyWordCounter]
        val child = expectMsgType[ActorRef]

        supervisor ! Props[FussyWordCounter]
        val secondChild = expectMsgType[ActorRef]

        secondChild ! "Testing supervisor"
        secondChild ! Report
        expectMsg(2)


        EventFilter[NullPointerException]() intercept{
          child ! ""
        }
        Thread.sleep(500)
        secondChild ! Report
        expectMsg(0)

      }
    }
  }

}
object SupervisionSpec{

  class Supervisor extends Actor {
    override val supervisorStrategy = OneForOneStrategy() {
      case _:NullPointerException => {
        println("restart")
        Restart
      }
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

  class NoDeathOnRestartSupervisor extends Supervisor{
    override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
      // empty
    }
  }

  class AllForOneSupervisor extends Actor {
    override val supervisorStrategy = AllForOneStrategy() {
      case _:NullPointerException => {
        println("restart")
        Restart
      }
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
