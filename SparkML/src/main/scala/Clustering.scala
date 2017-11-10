
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.SparkSession

import scala.collection.mutable.{ArrayBuffer, ListBuffer}


object Clustering {

  case class Mapping(group: Int, id: Int)

  val splitRegex = " |\\.|!|,"

  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder.master("local").appName("Spam Clustering").getOrCreate()

    val spamPath = getClass.getResource("/spam.txt").getPath
    val hamPath = getClass.getResource("/ham.txt").getPath

    val spamInput = ss.read.textFile(spamPath).collect()
    val hamInput = ss.read.textFile(hamPath).collect()
    val allInput = spamInput ++ hamInput

    val allWords = (spamInput ++ hamInput).flatMap(line => line.split(splitRegex)).toSet.toList.sorted

    val spamVector = createVector(spamInput, allWords)
    val hamVector = createVector(hamInput, allWords)
    val allVector = spamVector ++ hamVector

    val spamDenseVectors = spamVector.map(v => Vectors.dense(v.toArray))
    val hamDenseVectors = hamVector.map(v => Vectors.dense(v.toArray))

    val combinedData = spamDenseVectors ++ hamDenseVectors

    val numClusters = 4
    val numIterations = 50
    val clusters = KMeans.train(ss.sparkContext.parallelize(combinedData), numClusters, numIterations)

    val mapping = ListBuffer.empty[Mapping]
    for (i <- allInput.indices) {
      mapping += Mapping(group = clusters.predict(combinedData(i)), id = i)
    }

    val mappings = mapping.groupBy(_.group)
    for (group <- mappings.keys) {
      println(s"\n\nGroup: $group")
      for (mailMapping <- mappings(group)) {
        println(allInput(mailMapping.id))
      }
    }
  }

  private def createVector(spamInput: Array[String], allWords: List[String]): Seq[ArrayBuffer[Double]] = {
    val resultVector = Seq.fill(spamInput.length)(ArrayBuffer.fill(allWords.size)(0.0))
    for (i <- spamInput.indices) {
      spamInput(i).split(splitRegex).foreach { word =>
        val vector = resultVector(i)
        vector(allWords.indexOf(word)) += 1.0
      }
    }
    resultVector
  }
}