package Models.PolyMorphism

case class Circle(radius:Double) extends Shape{

  override def getArea: Double = Math.PI * radius*radius
}
