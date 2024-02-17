package AkkaTesting

import AkkaTesting.BasicSpec.{Blackhole, LabTestActor, SimpleActor}
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._
import scala.language.{existentials, postfixOps}
import scala.math.random
import scala.util.Random

class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  // setup
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A simple actor" should {
    "send back the same message" in {
      val echoActor = system.actorOf(Props[SimpleActor])
      val message = "Hello,test"
      echoActor ! message

      expectMsg(message)
    }
    "send back the 0 number" in {
      val echoActor = system.actorOf(Props[SimpleActor])
      val number = 12
      echoActor ! number

      expectMsg(0)
    }
    " A blackhole actor" should {
      "send back some message" in {
        val bhActor = system.actorOf(Props[Blackhole])
        val message = "Hello,test"
        bhActor ! message

        expectNoMessage(1 second)
      }

    }

    " A lab test actor" should {
      val ltActor = system.actorOf(Props[LabTestActor])
      "reply to greeting" in {
        val message = "greeting"
        ltActor ! message

        expectMsgAnyOf("hi","hello")
      }
      "reply with favorite tech" in {
        val message = "favoriteTech"
        ltActor ! message

        expectMsgAllOf("Scala","Akka")
      }

      "reply with cool tech in a different way" in {
        val message = "favoriteTech"
        ltActor ! message
        val recMessages = receiveN(2) //Seq[Any]

        // free to do more complicated assertions

      }

      "reply with cool tech in a fancy way" in{
        ltActor ! "favoriteTech"
        expectMsgPF(){
          case "Scala" =>
          case "Akka" =>
        }
      }

    }

  }

}
object BasicSpec{
  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message:String => sender() ! message
      case number:Int => sender() ! 0
    }
  }
  class Blackhole extends Actor {
    override def receive: Receive = Actor.emptyBehavior
  }

  class LabTestActor extends Actor {
    val random = new Random()
    override def receive: Receive = {
      case "greeting" =>
        if(random.nextBoolean()) sender() ! "hi" else sender() ! "hello"

      case "favoriteTech" =>
        sender() ! "Scala"
        sender() ! "Akka"
    }
  }
}
