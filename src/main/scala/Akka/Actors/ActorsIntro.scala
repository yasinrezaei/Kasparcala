package Akka.Actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App {

  val actorSystem = ActorSystem("FirstAS")
  println(actorSystem.name)

  /**
   * actors are uniquely identified
   * messages are async
   * each actor may respond differently
   * actors are encapsulated
   */

  //word count actor
  class WordCountActor extends Actor {
    //internal data
    var totalWords = 0

    //behavior
    override def receive: PartialFunction[Any,Unit] = {
      case message: String =>
        println(s"[word counter] I have received ${message}")
        totalWords+= message.split(" ").length
      case msg => println(s"[word counter] I cant understand ${msg.toString}")
    }

  }


  val wordCounter = actorSystem.actorOf(Props[WordCountActor],"wordCounter")
  val anotherWordCounter = actorSystem.actorOf(Props[WordCountActor],"anotherWordCounter")

  wordCounter ! "I am yasin rezaei"
  wordCounter ! "Bib bib"
  anotherWordCounter ! "Haha Again"

  object Person{
    def props(name:String) = Props(new Person(name))
  }
  class Person(name:String) extends Actor{
    override def receive: Receive = {
      case "Hi" => println(s"Hi, my name is ${name}")
    }
  }

  val person = actorSystem.actorOf(Person.props("yasin"))
  person ! "Hi"




}
