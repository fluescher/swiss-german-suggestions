import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.SparkSession

object Spam {

  def main(args: Array[String]) {
    val ss = SparkSession.builder.master("local").appName("Spam Detector").getOrCreate()

    val spamPath = getClass.getResource("/spam.txt").getPath
    val hamPath = getClass.getResource("/ham.txt").getPath

    val spam = ss.read.textFile(spamPath).rdd
    val ham = ss.read.textFile(hamPath).rdd

    val tf = new HashingTF(numFeatures = 100)
    val spamFeatures = spam.map(email => tf.transform(email.split(" ")))
    val hamFeatures = ham.map(email => tf.transform(email.split(" ")))

    val positiveExamples = spamFeatures.map(features => LabeledPoint(1, features))
    val negativeExamples = hamFeatures.map(features => LabeledPoint(0, features))
    val trainingData = positiveExamples ++ negativeExamples

    val lrLearner = new LogisticRegressionWithLBFGS
    val model = lrLearner.run(trainingData)

    val posTestExample1 = tf.transform("O M G GET money money cheap stuff by sending money to ...".split(" "))
    val negTestExample1 = tf.transform("Hi Dad, I started studying Spark the other day ...".split(" "))
    val posTestExample2 = tf.transform("Buy and subscribe today to get instant money! 2000 Dollars NOW!".split(" "))
    val negTestExample2 = tf.transform("Hi Thomas, I just got our new Spark tool running.".split(" "))

    model.clearThreshold()
    println(s"Prediction for positive test example: ${model.predict(posTestExample1)}")
    model.clearThreshold()

    println(s"Prediction for negative test example: ${model.predict(negTestExample1)}")
    model.clearThreshold()

    println(s"Prediction for positive test example: ${model.predict(posTestExample2)}")
    model.clearThreshold()

    println(s"Prediction for negative test example: ${model.predict(negTestExample2)}")

    ss.stop()
  }
}
