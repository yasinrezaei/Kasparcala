package AkkaMonitor

import AkkaMonitor.StartStopActor.Parent.{StartChild, StopChild}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, PoisonPill, Props, Terminated}

object StartStopActor extends App {

  val system = ActorSystem("StopActorDemo")

  object Parent{
    case class StartChild(name:String)
    case class StopChild(name:String)
    case object Stop

  }

  class Parent extends Actor with ActorLogging {

    override def receive: Receive = withChildren(Map())

    def withChildren(children:Map[String,ActorRef]):Receive ={
      case StartChild(name) =>
        println(s"start child $name")
        context.become(withChildren(children + (name -> context.actorOf(Props[Child],name))))
      case StopChild(name) =>
        println(s"stopping child with name $name")
        val childOption = children.get(name)
        childOption.foreach(childRef => {
          context.stop(childRef)
        })
    }
  }

  class Child extends Actor with ActorLogging{

    override def receive: Receive = {
      case message => println(s"[$self] : i receive message : $message")
    }
  }


  val parent = system.actorOf(Props[Parent],"parent")
  parent ! StartChild("yasin")
  parent ! StartChild("benyamin")


  val yasin = system.actorSelection("/user/parent/yasin")
  val benyamin = system.actorSelection("/user/parent/benyamin")
  yasin ! "hello yasin"

  parent ! StopChild("yasin")
  Thread.sleep(5000)
  for (i <- 1 to 500) yasin ! i+"hey yasin "
  //benyamin ! "test"
  // benyamin ! PoisonPill
  //benyamin ! Kill
  //benyamin ! "test2"


  class Watcher extends Actor {

    override def receive: Receive = {
      case StartChild(name) => {
        val child = context.actorOf(Props[Child],name)
        println(s"start and watch child $name")
        context.watch(child)
      }
      case Terminated(ref) => {
        println(s"the reference that i watching $ref has been stopped")
      }
    }
  }

  val watcher = system.actorOf(Props[Watcher],"watcher")
  watcher ! StartChild("w-child")
  val watchedChild = system.actorSelection("/user/watcher/w-child")

  Thread.sleep(500)
  watchedChild ! PoisonPill


}
