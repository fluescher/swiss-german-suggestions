import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}


object MovieData {

  def main(args: Array[String]) {
    val spark = SparkSession.builder.master("local").appName("Simple Application").getOrCreate()

    val movies = getClass.getResource("/movies.csv").getPath
    val credits = getClass.getResource("/credits.csv").getPath

    def readCsv(path: String): DataFrame = {
      spark.read.option("header", true).option("quote", "\"").option("escape", "\"").csv(path)
    }


    val moviesDf = readCsv(movies)
    val creditsDf = readCsv(credits)
    val df = moviesDf.join(creditsDf.withColumnRenamed("movie_id", "id"), "id")

    df.printSchema()

    df
      .where(col("budget") < 1000000)
      .show(10, truncate = false)

    spark.stop()
  }
}