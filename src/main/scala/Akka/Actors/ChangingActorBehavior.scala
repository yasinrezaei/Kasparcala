package Akka.Actors

import Akka.Actors.ChangingActorBehavior.Mom.MomStart
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChangingActorBehavior extends App {

  object FussyKid{
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }
  class FussyKid extends Actor {
    import FussyKid._
    import Mom._
    //internal state of the kid
    var state = HAPPY
    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(message) => {
        if (state==HAPPY) sender() ! KidAccept
        else sender() ! KidReject
      }


    }
  }

  class StatelessFussyKid extends Actor{
    import FussyKid._
    import Mom._
    override def receive: Receive = happyReceive
    def happyReceive:Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false)
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept
    }
    def sadReceive:Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false) //if false => stack.push(happyReceive)
      case Food(CHOCOLATE) => //context.unbecome()
      case Ask(_) => sender() ! KidReject
    }
  }

  object Mom{
    case class MomStart(kidRef:ActorRef)
    case class Food(food:String)
    case class Ask(message:String)
    val VEGETABLE= "veggies"
    val CHOCOLATE = "CHOCOLATE"
  }

  class Mom extends Actor{
    import Mom._
    import FussyKid._
    override def receive: Receive = {
      case MomStart(kidRef) => {

        kidRef ! Food(VEGETABLE)
        kidRef ! Ask("do you want to play?")

      }
      case KidAccept => println("Yay, my kid is happy!")
      case KidReject => println("Yay, my kid is sad!")
    }
  }



  val actorSystem = ActorSystem("mom_kid")
  val fussyKid = actorSystem.actorOf(Props[FussyKid])
  val statelessFussyKid = actorSystem.actorOf(Props[StatelessFussyKid])
  val mom = actorSystem.actorOf(Props[Mom])

  mom ! MomStart(statelessFussyKid)

}
