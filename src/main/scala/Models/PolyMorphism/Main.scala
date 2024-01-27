package Models.PolyMorphism

object Main {
  def main(args: Array[String]): Unit = {
    val s = Square(3)
    val c = Circle(4)
    println("- Area is "+printArea(s))
    println("- Area is "+printArea(c))

  }

  def printArea[T <: Shape](shape: T) : Double = {
    print(shape.getClass.getName)
    shape.getArea
  }

}
