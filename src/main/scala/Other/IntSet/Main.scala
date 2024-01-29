package Other.IntSet

object Main {
  def main(args: Array[String]): Unit = {
      val t1 = new NonEmpty(3,new Empty,new Empty)
      val t2 = t1 incl 4 incl 5 incl 1
      print(t2.toString)
    
  }

}
