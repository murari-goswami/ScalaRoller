package com.company.analytics.util

import java.io.{ObjectInputStream, ObjectOutputStream, PrintWriter}

import com.company.analytics.util.DFSWrapper.ConfigurationWrapper
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import scala.io.Source


object DFSWrapper {

  def apply(config: Configuration): DFSWrapper = {
    new DFSWrapper(new ConfigurationWrapper(config))
  }

  final class ConfigurationWrapper(@transient var config: Configuration) extends Serializable {

    //noinspection ScalaUnusedSymbol
    private def writeObject(out: ObjectOutputStream): Unit = {
      config.write(out)
    }

    //noinspection ScalaUnusedSymbol
    private def readObject(in: ObjectInputStream): Unit = {
      config = new Configuration(false)
      config.readFields(in)
    }
  }


  implicit class ExtendedFileSystem(fs: FileSystem) {

    def writeFile(path: Path, content: String): Unit = {
      val outStream = fs.create(path)
      val writer = new PrintWriter(outStream)
      writer.write(content)
      writer.close()
    }

    def readFile(path: Path): String = {
      Source.fromInputStream(fs.open(path)).mkString
    }
  }
}


class DFSWrapper private(config: ConfigurationWrapper) extends Serializable {

  @transient private var fs: FileSystem = _

  def getFileSystem: FileSystem = {
    if (fs == null) {
      fs = FileSystem.get(config.config)
    }
    fs
  }
}
