package Other.Scala_Collections

object Main {

  def main(args: Array[String]): Unit = {

    val nums: Vector[Int] = Vector(1, 2, 3, 4)
    println(nums)

    val new_nums = nums :+ 5
    println(new_nums)

    // List, Vector => Seq
    // Set,Seq,Map => Iterable

    var xs:Array[Int] = Array(1,2,3)
    xs = xs map (x=>x*2)
    //xs = xs filter (x => x==2)

    val s = "Yasin"
    val c = s filter(ch => ch.isUpper)
    println(c)

    //String and Array are come from java


    val r:Range = 1 until 5 by 2
    println(r)

    xs foreach(x => print(x))
    s foreach(ch => println(ch))

    println("----------------------------------")
    var v1 = Vector(1,2,3)
    var v2 = Vector(2,3,4)
    println("Zip = ",v1 zip v2)
    println("Map = ",v1 map(x => x*2))
    println("Sum = ",v1.sum)
    println("Product = ",scalarProduct(Vector(1,2,3),Vector(2,3,4) ))

    println("----------------------------------")
    val n=7
    var res = (1 until n) map (i =>
      (1 until i) map(j=>(i,j)))
    println(res)

    println("----------------------------------")

    val names = Set("ali", "yasin", "reza","yasin")


    for{
      n1 <- names
      n2 <- names
      if n1!=n2
    } println(n1," ",n2)

    val s1 = (1 to 4 ).toSet
    println("----------------------------------")

    for{
      n1 <-names.withFilter(x=>x=="yasin")
      n2 <-names if n2 startsWith "r"
      m = n1+n2
    } println(m)
    //println(names map (x => x*2))


    println("----------------------------------")

    val capitalOfCountry = Map("US"-> "Washington","IR" -> "Tehran")
    //println(capitalOfCountry get "US") //Some

    val m = List("apple","samsung","mi","jlx","j4x")
    val m_sorted = m sortWith (_.length < _.length)
    val m_grouped = m groupBy(_.head)

    val t1: Map[Int, Int] = Map(1 -> 10, 2 -> 15,3->12)
    val t2: Map[Int, Int] = Map(1 -> 2, 2 -> 4)

    //println(t1++t2)

    val mymap = Map( 1 -> "ABC" , 2 -> "DEF", 3 -> "GHI")
    val mymap_reversed = for((digit,str) <- mymap ; itr <- str) yield itr -> digit
    //print(mymap_reversed)

    val dd = ((1000 to 10000) filter (x => x%2==0))(1)
    val ss = ((1000 to 10000).toStream filter (x => x%2==0))(1)

    //println(ss)

    def expr = {
      val x = {print("x");1}
      lazy val y = {print("y");2}
      def z = {print("z");3}

      z+y+x+z+y+x
    }
    //expr

    def from(n:Int):Stream[Int] = n #:: from(n+1)
    val nats = from(3)
    val m4s = nats map (_*4)
    //print(m4s(3))

    def loop(s:Int,e:Int):Unit  = {
      print(s)
      if (s!=e ) loop(s+1,e)
    }

    loop(2,10)

  }

  def scalarProduct(xs:Vector[Double],ys:Vector[Double]):Double = {
    (xs zip ys).map(xy => xy._1 * xy._2).sum
  }

}
