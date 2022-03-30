import java.io.{BufferedReader, File, FileReader}
import scala.annotation.tailrec
import scala.util.{Try, Using}
import scala.io.StdIn.readLine
import scala.sys.exit

object Main extends App {
  Program.readFile(args)
    .fold(
      println,
      file => Program.iterate(Program.index(file))
    )
}

object Program {
  type Word = String
  type FileName = String
  case class Index(indexMap: Map[Word, Set[FileName]])

  sealed trait ReadFileError

  case object MissingPathArg extends ReadFileError
  case class NotDirectory(error: String) extends ReadFileError
  case class FileNotFound(t: Throwable) extends ReadFileError

  def readFile(args: Array[String]): Either[ReadFileError, File] = {
    for {
      path <- args.headOption.toRight(MissingPathArg)
      file <- Try(new java.io.File(path))
        .fold(
          throwable => Left(FileNotFound(throwable)),
          file =>
            if (file.isDirectory) Right(file)
            else Left(NotDirectory(s"Path [$path] is not a directory"))
        )
    } yield file
  }

  def index(file: File): Index = {
    val arrayOfFiles = file.listFiles()
    val indexMap = arrayOfFiles.foldLeft(Map[Word, Set[FileName]]()){ case (accIndex, currentFile) =>
      val words = getWords(currentFile).flatMap(w => w.split(" |\\\\t")).map(w => w.trim)
      words.foldLeft(accIndex) {
        case (accMap, currentWord) => accMap.get(currentWord) match {
          case Some(fileSet) => accMap + (currentWord -> (fileSet + currentFile.getName))
          case None => accMap + (currentWord -> Set(currentFile.getName))
        }
      }
    }
    Index(indexMap)
  }

  def getWords(file: File): Set[String] = {
    val lines = Using.resource(new BufferedReader(new FileReader(file))) { reader =>
      Iterator.continually(reader.readLine()).takeWhile(_ != null).toSet
    }
    lines
  }

  def simpleSearch(searchWords: List[String], indexedFiles: Index): Map[FileName, Double] = {
    searchWords.foldLeft(Map[FileName, Double]()) { case (accMap, currentString) =>
      indexedFiles.indexMap.get(currentString) match {
        case Some(setOfFiles) => setOfFiles.foldLeft(accMap) { case (fileMap, currentFile) =>
          accMap.get(currentFile) match {
            case Some(counter) => fileMap + (currentFile -> (counter + 1.0))
            case None => fileMap + (currentFile -> 1.0)
          }
        }
        case None => Map()
      }
    }
  }

  def advancedSearch: String = "Under construction....."

  def cmd(inputCommand: String, searchWords: List[String], indexedFiles: Index): Unit = inputCommand match {
    case "simple" =>
      val results = simpleSearch(searchWords, indexedFiles)
      results.foreach { case (file, count) =>
        println(s"$file rank: ${(count/searchWords.length)*100}%%")
      }
    case "advanced" => println(advancedSearch)
    case _ => println("Command not found...")
  }

  def helpMenu: String = "available commands:\n :quit \n --help \n simple [arg1] [arg2] \n advanced"
  @tailrec
  def iterate(indexedFiles: Index): Unit = {
    print(s"search> ")
    val searchString = readLine()
    searchString.trim.split(" ").toList match {
      case ":quit" :: _ => println("exiting")
        exit(0)
      case "--help" :: _ => println(helpMenu)
      case Nil => println("No input provided\n"+helpMenu)
      case _ :: Nil => println("No search words provided\n"+helpMenu)
      case head :: tail => cmd(head, tail, indexedFiles)
    }
    iterate(indexedFiles)
  }
}