package Spark.src.dataframes

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{DoubleType, IntegerType, LongType, StringType, StructField, StructType}

object Main extends App {

  val spark = SparkSession.builder()
    .appName("DataframesBasics")
    .config("spark.master","local")
    .getOrCreate()

  val df = spark.read
    .format("json")
    .option("inferSchema","true")
    .load("src/main/resources/data/cars.json")

  // showing a df
  df.show()
  df.printSchema()

  // get rows
  df.take(10).foreach(println)


  // spark types
  val longType = LongType
  val carsSchema = StructType(
    Array(
      StructField("Name", StringType), //nullable = false
      StructField("Miles_per_Gallon", IntegerType),
      StructField("Cylinders", IntegerType),
      StructField("Displacement", IntegerType),
      StructField("Horsepower", IntegerType),
      StructField("Weight_in_lbs", IntegerType),
      StructField("Acceleration", DoubleType),
      StructField("Origin", StringType)

    )
  )

  // obtain a schema
  val dfSchema = df.schema

  val carsDf = spark.read
    .format("json")
    .schema(carsSchema)
    .load("src/main/resources/data/cars.json")
  carsDf.show()

  val myRows= Seq (
    ("test",1,"test"),
    ("test",1,"test")
  )
  val manualDf = spark.createDataFrame(myRows)
  manualDf.show()


  import spark.implicits._
  val manualDfWithImplicits = myRows.toDF("n1","n2","n3")
  manualDfWithImplicits.show()
  println(manualDfWithImplicits.schema)




  scala.io.StdIn.readLine()


}
