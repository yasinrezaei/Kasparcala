package Models.PolyMorphism

case class Square(side:Double) extends Shape {
  override def getArea: Double = side*side
}
