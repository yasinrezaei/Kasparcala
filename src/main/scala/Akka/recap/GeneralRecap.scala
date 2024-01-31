package Akka.recap

import scala.concurrent.Future

object GeneralRecap extends App {

  //Partial Functions
  val partialFunction:PartialFunction[Int,Int] = {
    case 1 => 10
    case 2 => 12
  }

  val modifiedList = List(1,2,3).map{
    case 1 => 10
    case _ => 20
  }

  // lifting
  val lifted = partialFunction.lift
  //lifted(3) => None

  val pfChain = partialFunction.orElse[Int,Int]{
    case 60 => 100
  }


  //type aliases
  type ReceiveFunction = PartialFunction[Any,Any]
  def receive :ReceiveFunction = {
    case 1 => println("1")
  }


  //implicits
  implicit val timeout = 3000
  def setTimeout(f:() => Unit)(implicit timeout:Int) = {
    println(timeout)
    f()
  }
  setTimeout(()=>println("timeout"))

  //implicit conversions
  // 1) implicit defs
  case class Person(name:String){
    def greet = s"hi my name is $name"
  }
  implicit def fromStringToPerson(string:String):Person = Person(string)
  "Peter".greet

  // 2) implicit classes
  implicit class Dog(name:String){
    def bark= println("Bark")
  }
  "Lassie".bark


  // organize
  // local scope
  implicit val inverseOrdering:Ordering[Int] = Ordering.fromLessThan(_>_)
  List(1,2,3).sorted

  //imported scope
  import scala.concurrent.ExecutionContext.Implicits.global
  val future = Future{
    println("Hello Future")
  }

  //companian objects
  object Person{
    implicit val personOrdering:Ordering[Person] = Ordering.fromLessThan((a,b) => a.name.compareTo(b.name)<0)
  }
  List(Person("Bob"),Person("Alice")).sorted


}
