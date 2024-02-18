package AkkaTesting

import AkkaTesting.TimedAssertionSpec.{WorkResult, WorkerActor}
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

import scala.util.Random
import scala.concurrent.duration._
import scala.language.postfixOps

class TimedAssertionSpec extends TestKit(ActorSystem("TimedAssertionSpec"))
with ImplicitSender
with AnyWordSpecLike
with BeforeAndAfterAll {
  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)

  }
  "A worker actor" should{
    val workerActor = system.actorOf(Props[WorkerActor])
    "reply with the meaning of lief in a timely manner" in {
      within(500 millis,1 second){
        workerActor ! "work"
        expectMsg(WorkResult(42))
      }
    }

    "reply with valid work at a reasonable cadence" in {
      within(1 second){
        workerActor ! "workSequence"
        val results: Seq[Int] = receiveWhile[Int](max = 2 second, idle = 500 millis, messages = 10) {
          case WorkResult(result) => result
        }
        assert(results.sum > 5)
      }
    }

    "reply to a test probe in a timely manner" in {
      within(1 second){
        val probe = TestProbe()
        probe.send(workerActor, "work")
        probe.expectMsg(WorkResult(42))
      }
    }

  }


}
object TimedAssertionSpec{
  case class WorkResult(result: Int)
  class WorkerActor extends Actor {
    override def receive: Receive = {
      case "work" =>
        Thread.sleep(500)
        sender() ! WorkResult(42)
      case "workSequence" =>
        val r = new Random()
        for(_ <- 1 to 10){
          Thread.sleep(r.nextInt(50))
          sender() ! WorkResult(1)
        }
    }
  }


}
