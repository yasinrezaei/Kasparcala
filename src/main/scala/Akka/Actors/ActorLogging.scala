package Akka.Actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

object ActorLogging extends App {
  class SimpleActor extends Actor with ActorLogging {
    // explicit logging
    val logger = Logging(context.system, this)
    override def receive: Receive = {
      // explicit logging
      case message:String => logger.info(message.toString)//log it
      //actor logging
      case number:Int => log.warning("Number!")
    }
  }

  val system =ActorSystem("LoogingDemo")
  val simpleActor = system.actorOf(Props[SimpleActor])
  simpleActor ! "my message"
  simpleActor ! 12

}
