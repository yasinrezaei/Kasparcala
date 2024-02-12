package Akka.Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCapabilities extends App {
  class SimpleActor extends Actor{
    override def receive: Receive = {
      case "Hi!" =>{
        println(s"[${self}] I have received Hi!")
        sender() ! "Hello,There!"
      }

      case message:String =>
        println(s"[${self}] I have received $message")
      case number:Int =>
        println(s"[${self}] I have received a number: $number")
      case SpecialMessage(contents) =>
        println(s"[${self}] I have received special message: $contents")
      case SendMessageToYourself(content) =>
        self ! content
      case SayHiTo(ref) => ref ! "Hi!"
      case WirelessPhoneMessage(content,ref) =>{
        ref forward content+"s"
      } // i keep the original sender
    }
  }



  val actorSystem = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = actorSystem.actorOf(Props[SimpleActor],"simpleActor")
  // 1) messages can be of any type
  //messages must be IMMUTABLE
  //messages must be SERIALIZABLE

  //simpleActor !"hello+"
  //simpleActor ! 23
  case class SpecialMessage(contents:String)
  //simpleActor ! SpecialMessage("some special content")


  // 2) actors have information about their context
  // context.self === this
  case class SendMessageToYourself(content:String)
  //simpleActor ! SendMessageToYourself("i am an actor")

  // 3) actors can reply to messages
  val alice = actorSystem.actorOf(Props[SimpleActor],"Alice")
  val bob = actorSystem.actorOf(Props[SimpleActor],"Bob")

  case class SayHiTo(ref:ActorRef)
  //alice ! SayHiTo(bob)


  // 4) dead letters
  //alice ! "Hi!" //reply to "me"

  // 5) forwarding messages
  //D->A->B
  // forwarding = sending a message with the original sender
  case class WirelessPhoneMessage(content:String,ref:ActorRef)
  alice ! WirelessPhoneMessage("Hi",bob)





}
