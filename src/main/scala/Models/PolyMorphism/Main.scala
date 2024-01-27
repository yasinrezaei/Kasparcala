package Models.PolyMorphism

object Main {
  def main(args: Array[String]): Unit = {
    val s = Square(3)
    val c = Circle(4)
    println("- Area is "+printArea(s))
    println("- Area is "+printArea(c))

  }
  // S <: T Means S is a subtype of T
  // S >: T Means S is a supertype of T, or T is subtype of S
  def printArea[T <: Shape](shape: T) : Double = {
    print(shape.getClass.getName)
    shape.getArea
  }

}
