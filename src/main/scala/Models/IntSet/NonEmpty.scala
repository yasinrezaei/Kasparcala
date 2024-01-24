package Models.IntSet

class NonEmpty(elem:Int,left:IntSet,right:IntSet) extends IntSet {

  override def incl(x: Int): IntSet = {
    if(x<elem) new NonEmpty(elem,left incl x,right)
    else if (x>elem) new NonEmpty(elem, left, right incl x)
    else this
  }

  override def contains(x: Int): Boolean = {
    if(x<elem) left contains x
    else if (x>elem) right contains x
    else true
  }

  override def toString: String = "{"+left+" "+elem+" "+right+"}"
}
