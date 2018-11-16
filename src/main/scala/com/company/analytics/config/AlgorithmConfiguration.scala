package com.company.analytics.config

import org.apache.hadoop.fs.Path
import java.text.DecimalFormatSymbols
import com.company.analytics.util.DFSWrapper
import com.company.analytics.util.DFSWrapper._
import scala.util.parsing.json.{JSON, JSONArray, JSONObject}


class AlgorithmConfiguration(dfs: DFSWrapper, fileName: String) extends Serializable {

  private val decimalSeparator: Char = new DecimalFormatSymbols().getDecimalSeparator

  JSON.globalNumberParser = (in: String) => if (in.contains(decimalSeparator)) in.toDouble else in.toInt

  private lazy val config = {
    val jsonContent = dfs.getFileSystem.readFile(new Path(fileName))
    JSON.parseRaw(jsonContent) match {
      case Some(JSONObject(obj)) => obj
      case _ => throw new IllegalArgumentException(s"Wrong format of the configuration file: $jsonContent")
    }
  }

  def getAsSeq[T](propertyName: String): Seq[T] = {
    config.get(propertyName) match {
      case Some(JSONArray(list)) => list.map(_.asInstanceOf[T])
      case _ => throw new IllegalArgumentException(s"Unable to find configuration property $propertyName")
    }
  }

  def getAs[T](propertyName: String): T = {
    config.get(propertyName) match {
      case Some(property) => property.asInstanceOf[T]
      case None => throw new IllegalArgumentException(s"Unable to find configuration property $propertyName")
    }
  }

  def contains(propertyName: String): Boolean = {
    config.contains(propertyName)
  }
}
