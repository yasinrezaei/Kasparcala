package Akka.Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorsExercise extends App {
  // Distributed word counting
  object WordCounterMaster{
    case class Initialize(nChildren:Int)
    case class WordCountTask(id:Int, text:String)
    case class WordCountReply(id:Int, count:Int)
  }
  class WordCounterMaster extends Actor{
    import WordCounterMaster._
    override def receive: Receive = {
      case Initialize(nChildren) =>{
        println("[master] initializing ...")
        var workersList = for(i <- 1 to nChildren) yield context.actorOf(Props[WordCounterWorker],s"worker_$i")
        context.become(taskScheduler(workersList,0,0,Map()))
      }




    }
    def taskScheduler(workers: Seq[ActorRef],currentWorkerIndex:Int,currentTaskId:Int, requestMap:Map[Int,ActorRef]):Receive ={

      case text:String =>
        println(s"[master] I hasve received: $text - i will send it to worker $currentWorkerIndex")
        val originalSender = sender()
        val task = WordCountTask(currentTaskId, text)
        workers(currentWorkerIndex) ! task
        val nextWorkerIndex = (currentWorkerIndex+1) % workers.length
        val newTaskId = currentTaskId+1
        val newRequestMap = requestMap +(currentTaskId -> originalSender)
        context.become(taskScheduler(workers, nextWorkerIndex,newTaskId,newRequestMap))

      case WordCountReply(id,count) =>
        println(s"[master] i have received a reply for task $id with count = $count")
        val originalSender = requestMap(id)
        originalSender ! count
        context.become(taskScheduler(workers, currentWorkerIndex, currentTaskId, requestMap-id))

    }
  }
  class WordCounterWorker extends Actor{
    import WordCounterMaster._
    override def receive: Receive = {
      case WordCountTask(id, text) => {
        println(s"[${self.path}] i have received task $id with $text")
        sender() ! WordCountReply(id, text.split(" ").length)
      }
    }
  }


  class TestActor extends Actor{
    import WordCounterMaster._
    override def receive: Receive = {
      case "go" =>
        val master = context.actorOf(Props[WordCounterMaster],"master")
        master ! Initialize(3)
        val texts = List("I love akka","this is yasin","hi","haha haha haha by")
        texts.foreach(text => master ! text)
      case count:Int => println(s"[test actor] reply = $count")
    }
  }

  val system = ActorSystem("round_robin_word_count")
  val testActor = system.actorOf(Props[TestActor],"test_actor")

  testActor ! "go"


}
