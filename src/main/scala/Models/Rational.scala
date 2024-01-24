package Models

class Rational(x:Int,y:Int) {

  require(y!=0, "denominator must be non zero")
  // assert(x<10)

  //another constructor
  def this(x:Int) = this(x,1)

  private def gcd(a:Int, b:Int):Int = if(b==0) a else gcd(b,a%b)
  private val g = gcd(x,y)

  def numer: Int = x/g
  def denom: Int = y/g

  def add(that:Rational):Rational =
    new Rational(
      this.numer * that.denom + that.numer * this.denom, this.denom * that.denom
    )
  def neg:Rational = new Rational(-numer,denom)
  def sub(that:Rational) = add(that.neg)


  //operators
  def +(that: Rational): Rational = new Rational(
    this.numer * that.denom + that.numer * this.denom, this.denom * that.denom
  )

  def - (that:Rational): Rational = this + (that.neg)



  // function overriding
  override def toString: String = s" numer = $numer denom = $denom "
  def less(that:Rational) = numer*that.denom < that.numer * denom


  //function overloading
  def test(x:Int) = x
  def test(x:Int,y:Int) = x+y


}

