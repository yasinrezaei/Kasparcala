object Utils {
  def map[T](xs:List[T])(f: T => T):List[T] = xs match {
    case Nil => xs
    case y::ys => f(y) :: map(ys)(f)
  }

  def pack [T](xs:List[T]) : List[List[T]] = xs match {
    case Nil => Nil
    case x::xs1 =>
      val (first, rest) = xs span(y => y==x)
      first :: pack(rest)
  }


  //reduction of lists

  def sum(xs:List[Int]):Int = xs match {
    case Nil => 0
    case y::ys => y + sum(ys)
  }

  def reduceLeft[T](xs:List[T])(op:(T,T) => T):T = xs match {
    case Nil => throw new Error("Nil.reduceLeft")
    case y::ys => (ys foldLeft y)(op)
  }

  def foldLeft[T,U](xs:List[T])(z:U)(op:(U,T) => U):U  = xs match {
    case Nil => z
    case y::ys => (foldLeft(ys)(op(z,y))(op))
  }

}
