package tictactoe

import java.io.File
import java.io.PrintWriter
import scala.io.Source
import java.io.FileWriter
import cats.instances.boolean
import java.nio.file.Files
import java.nio.file.Paths
//import scala.collection.parallel.CollectionConverters._
//TODO Removing par for the conception part 
object  DataBase {
//private val BASE : String ="driver/jvm/" 
  
private val BASE : String ="C:/Users/Mouktar/Desktop/webapp-main/driver/jvm/" 
  
/*Reads a file and returns a list of all lines*/
def readFile(fileAddress: String): List[String] = {
  Source.fromFile(BASE +fileAddress).getLines().toList
}

/*Creates an appender of a file, appends direclty a List Of Line if append is false it overides the file*/
def appender(fileAddress : String)(append : Boolean)(toAppend :Seq[String]) ={
val file = new File(BASE + fileAddress)
val writer = new PrintWriter(new FileWriter(file, append))
toAppend.foreach(writer.println)
writer.close()   
}

/*Finds a line that begins or contains the prefix and returns It into a list*/

def findLinePrefixed(fileAddress: String,prefix : String, beginsWith : Boolean) : Option[List[String]] = 
    val l =
    (for l <- readFile(fileAddress)
        if  (beginsWith && l.startsWith(prefix)) || (!beginsWith && l.contains(prefix))
    yield l)
    if l.isEmpty then None else Some(l)    

/**get the line idexed by the prefix followed by ":" without the index */
def getDataLinePrefixed(fileAddress : String, prefix : String ) : Option[List[String]] =
    val l = findLinePrefixed(fileAddress,prefix+":", true)
    l match
        case None => None
        case Some(value) => Some(value.map(line => line.drop(prefix.length()+1)))

/**replace a line that contains a prefix*/ 
def replaceLine(fileAddress : String, prefix : String, line : String)=
    val data = readFile(fileAddress)
    println("line:"+line)
    if data.forall(!_.contains(prefix)) then throw new Exception("Prefix "+prefix+ "unfoud in "+ fileAddress)
    val newData = data.map( e => if  e.contains(prefix) then line else e)
    println("------     --")
    newData.foreach(println)
    println("------     --")
    appender(fileAddress)(false)(newData) 
/**replace data for the given index */
def replaceDataLine(fileAddress :String, prefix: String, newData :String)=
    replaceLine(fileAddress,prefix+":",prefix+":"+" "+newData)


/** creates a new file in a given path*/
def createFile(filePath : String) = 
    Files.createDirectories(Paths.get(BASE + filePath).getParent());
    new java.io.File(BASE + filePath).createNewFile()


def fileExists(fileAddress: String): Boolean = 
  try {
    Files.exists(Paths.get(BASE + fileAddress))
  } catch {
    
    case e: Exception =>{
        println("nope")
        false
    } 
  }

}