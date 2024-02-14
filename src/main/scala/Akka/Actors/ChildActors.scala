package Akka.Actors

import Akka.Actors.ChildActors.CreditCard.{AttachToAccount, CheckStatus}
import Akka.Actors.ChildActors.Parent.{CreateChild, TellChild}
import Akka.Actors.ChildActors.bankAccountRef
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActors extends App {
  // actors can create other actors
  object Parent{
    case class CreateChild(name:String)
    case class TellChild(message:String)
  }
  class Parent extends Actor {
    import Parent._
    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} creating child")
        //create a new actor right HERE
        val childRef = context.actorOf(Props[Child],name)
        context.become(withChild(childRef))
    }
    def withChild(childRef:ActorRef): Receive = {
      case TellChild(message) => childRef forward(message)
    }
  }
  class Child extends Actor{
    override def receive: Receive = {
      case message => println(s"${self.path} I got : $message")
    }
  }


  val system = ActorSystem("ParentChild")
  val parent = system.actorOf(Props[Parent],"parent")
  parent ! CreateChild("child")
  parent ! TellChild("Hey kid")

  /**
  Guardian actors(top-level)
  - /system = system guardian
  - /user = user level guardian
  - / = the root guardian
   */


  /**
   * Actor selection
   */
  val childSelection = system.actorSelection("/user/parent/child")
  childSelection ! "I found you"


  /**
   * Danger
   *
   * Never pass mutable actor state. or ''This'' reference, to child actors
   *
   * Never in your life :)
   */

  object NaiveBankAccount{
    def props(accountId:String) = Props(new NaiveBankAccount(accountId))
    case class Deposit(amount:Int)
    case class Withdraw(amount:Int)
    case object InitializeAccount
  }
  class NaiveBankAccount(accountId:String) extends Actor {
    import CreditCard._
    import NaiveBankAccount._

    var amount = 0
    override def receive: Receive = {
      case InitializeAccount =>
        val creditCardRef = context.actorOf(Props[CreditCard],s"card-$accountId")
        //creditCardRef ! AttachToAccount() // !!

      case Deposit(funds) => deposit(funds)
      case Withdraw(funds) => withdraw(funds)
    }
    def deposit(funds:Int) = {
      println(s"${self.path} depositing $funds on top of $amount")
      amount += funds
    }
    def withdraw(funds:Int) = {
      println(s"${self.path} withdrawing $funds on top of $amount")
      amount -= funds
    }}
  object CreditCard{
    case class AttachToAccount(bankAccount: ActorRef)
    case object CheckStatus
  }
  class CreditCard extends Actor {
    override def receive: Receive = {
      case AttachToAccount(account) => context.become(attachedTo(account))
    }
    def attachedTo(account: ActorRef):Receive = {
      case CheckStatus =>
        println(s"[ ${self.path} ] your message has been processed")
    }
  }

  import NaiveBankAccount._
  import CreditCard._

  val bankAccountRef = system.actorOf(NaiveBankAccount.props("UI234"),"account")
  bankAccountRef ! InitializeAccount
  bankAccountRef ! Deposit(100)
//  Thread.sleep(500)
//  val ccSelection = system.actorSelection("/user/account/card")
//  ccSelection ! CheckStatus
}
