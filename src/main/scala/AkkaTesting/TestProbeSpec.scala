package AkkaTesting

import AkkaTesting.TestProbeSpec.{Master, Register}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class TestProbeSpec extends TestKit(ActorSystem("TestProbeSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll{

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TestProbeSpec._

  "A master actor" should {
    "register slave in " in {
      val master = system.actorOf(Props[Master])
      val slave =TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegisterationAck)
    }

    "send the work to the slave " in {
      val master = system.actorOf(Props[Master])
      val slave =TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegisterationAck)

      val workloadString = "i love you"
      master ! Work(workloadString)

      //The interaction between master and slave
      slave.expectMsg(SlaveWork(workloadString,testActor))
      slave.reply(WorkCompleted(3,testActor))

      expectMsg(Report(3))

    }

    "Aggregate data correctly" in{
      val master = system.actorOf(Props[Master])
      val slave =TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegisterationAck)

      val workloadString = "i love you"
      master ! Work(workloadString)
      master ! Work(workloadString)

      slave.receiveWhile(){
        case SlaveWork(`workloadString`,`testActor`) => slave.reply(WorkCompleted(3,testActor))
      }
      expectMsg(Report(3))
      expectMsg(Report(6))
    }
  }

}

object TestProbeSpec{
  case class Work(text:String)
  case class Report(totoalCount: Int)
  case class Register(slaveRef:ActorRef)
  case object RegisterationAck
  case class SlaveWork(text:String, originalRequester:ActorRef)
  case class WorkCompleted(count:Int, originalRequester:ActorRef)
  class Master extends Actor {

    override def receive: Receive = {
      case Register(slaveRef) =>
        sender() ! RegisterationAck
        context.become(online(slaveRef,0))
      case _ => //ignore
    }

    def online(slaveRef: ActorRef, totalWordCount: Int):Receive ={
      case  Work(text) => slaveRef ! SlaveWork(text, sender())
      case WorkCompleted(count,originalRequester) =>
        val newTotalWordCount = totalWordCount+count
        originalRequester ! Report(newTotalWordCount)
        context.become(online(slaveRef, newTotalWordCount))
    }
  }

  /**
  class Slave extends Actor {

  }
  */
}
