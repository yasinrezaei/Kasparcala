package Akka.recap

object pf extends App {
  val sumPositiveInts: PartialFunction[(Int, Int), Int] = {
    case (x, y) if x > 0 && y > 0 => x + y
  }

  //val result = sumPositiveInts.lift(2,3)
  def divide(x: Double, y: Double): Option[Double] =
    if (y != 0) Some(x / y) else None

  val result = divide(10, 0) match {
    case Some(value) => s"Result: $value"
    case None => "Cannot divide by zero"
  }

  println(result) // Output: Cannot divide by zero


}
