package AkkaMonitor

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}

object ActorLIfecycle extends App {

  object StartChild
  class LifecycleActor extends Actor with ActorLogging{

    override def preStart(): Unit = {
      println("Im starting ...")
    }

    override def postStop(): Unit = {
      println("I have stopped")
    }
    override def receive: Receive = {
      case StartChild =>
        context.actorOf(Props[LifecycleActor],"child")
    }
  }

  val system = ActorSystem("lifecycledemo")
  val parent  = system.actorOf((Props[LifecycleActor]),"parent")

  //parent ! StartChild
  //parent ! PoisonPill


  object Fail
  object FailChild
  class Parent extends Actor {
    private val child = context.actorOf(Props[Child],"supervisedChild")
    override def receive: Receive = {
      case FailChild => child ! Fail
    }
  }


  class Child extends Actor {
    override def preStart(): Unit = {
      println("supervised child started")
    }
    override def postStop(): Unit = {
      println("supervised child stoped")
    }

    override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
      println(s"supervised actor restarting :  ${reason.getMessage}")
    }

    override def postRestart(reason: Throwable): Unit = {
      println("supervised actor restarted")
    }
    override def receive: Receive = {
      case Fail =>
        println("child will fail")
        throw new RuntimeException("I failed")
    }
  }

  val supervisor = system.actorOf(Props[Parent])
  supervisor ! FailChild


}
