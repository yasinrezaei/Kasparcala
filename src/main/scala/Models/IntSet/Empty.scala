package Models.IntSet

class Empty extends IntSet {

  override def incl(x: Int): IntSet = new NonEmpty(x,new Empty,new Empty)

  override def contains(x: Int): Boolean = false

  override def toString: String = "."
}
