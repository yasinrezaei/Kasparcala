package Akka.Actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging
import com.typesafe.config.ConfigFactory

import scala.sys.Prop

object IntroAkkaConfig extends App {

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message:String => log.info(message.toString)//log it
      
    }
  }

  // inline configuration
  val configString =
    """
      |akka{
      |loglevel="DEBUG"
      |}
      |""".stripMargin
  val config = ConfigFactory.parseString(configString)
  val system = ActorSystem("ConfigDemo",ConfigFactory.load(config))
  val actor = system.actorOf(Props[SimpleActor])
  //actor ! "haha"

  // config file
  val defaultConfigFileSystem = ActorSystem("DCFsys")
  val actor2 = defaultConfigFileSystem.actorOf(Props[SimpleActor])
  //actor2 ! "remember me "


  // separate config in the same file
  val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
  val specialConfigFileSystem = ActorSystem("SCFsys",specialConfig)
  val actor3 = specialConfigFileSystem.actorOf(Props[SimpleActor])
  //actor3 ! "remember me special"

  //separate config in another file
  val specialConfig2 = ConfigFactory.load("secretFolder/secretConfig.conf")
  val specialConfigFileSystem2 = ActorSystem("SCFsys2",specialConfig2)
  val actor32 = specialConfigFileSystem2.actorOf(Props[SimpleActor])
  //actor32 ! "remember me special2"

  //different file formats
  /*
  json, properties, ...
   */
  val specialConfig3 = ConfigFactory.load("json/conf.json")
  val specialConfigFileSystem33 = ActorSystem("SCFsys23",specialConfig3)
  val actor323 = specialConfigFileSystem33.actorOf(Props[SimpleActor])
  actor323 ! "remember me special323"

}
