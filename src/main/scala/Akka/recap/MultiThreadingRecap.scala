package Akka.recap
import org.scalatest.{fullstacks, time}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
object MultiThreadingRecap extends App {

  val aThread = new Thread(()=> println("Im running in parallel"))

  //aThread.start()
  //aThread.join()

  val bThread = new Thread(()=>(1 to 1000).foreach(_ => println("Hello")))
  val cThread = new Thread(()=>(1 to 1000).foreach(_ => println("Bye")))

  //bThread.start()
  //cThread.start()

  //problem with threads => different runs produce different results

  class BankAccount(@volatile private var amount:Int) {
    override def toString: String = ""+amount
    def withdraw(money:Int) = this.amount -= money //its not ATOMIC

    def safeWithdraw(money:Int) = this.synchronized{
      this.amount -= money
    }

  }

  val future  = Future{
    //long computation - on a different thread
    42
  }
  //call back
  future.onComplete {
    case Success(42) => println("Data received")
    case Failure(_) => println("Exception happened")
  }




}
