package tictactoe

import javax.xml.crypto.Data
import scala.annotation.static
import java.sql.DatabaseMetaData
class User(var name : String, icon : String,var logs : (String,String),var friendList : List[Int],var  grailleLists : List[Int] ) {
  private val USERS_FOLDER_PATH = "users/"
  private val pathFile = USERS_FOLDER_PATH + this.hashCode()+".txt" 
  /** creates all the files requirement for a new user if it doesent exist, does nothig if it already exist */
  def create() = {
    val exists = DataBase.fileExists(pathFile)
    exists match
      case true =>
        println("file at address "+pathFile+ " already exists...Retrieving" )

      case false =>
        print("Noe exist")
        DataBase.createFile(pathFile)
        val appender = DataBase.appender(pathFile)(true)
        val listData = List(
          "name: "+name,
          "icon: "+icon,
          "logs: "+logs._1+","+logs._2,
          "friendList: "+ friendList.foldLeft("")((s,u) => s + "," ).drop(1),
          "grailleLists: " + grailleLists.foldLeft("")((s,g) => s + "," + g.hashCode()).drop(1)
        )
        appender(listData)
}
  def addFriend( friend : User)= {
    if (!friendList.contains(friend)) then print("Not currentrly friends with "+friend.name )
    friendList = friend.hashCode()::friendList
    update("friendsList")
  }
  def addGrailleList( graillelists : GrailleList)= {
    grailleLists = graillelists.hashCode() :: grailleLists
    update("grailleLists")
  }
  def removeFriend(friend : User) ={
   if (!friendList.contains(friend)) then print("Not currentrly friends with "+friend.name )
    friendList = friendList.filter( x=> !x.equals(friend))
    update("friendsList")

  }



  def update(dataType : String)={
    val lineData = 
      dataType match
        case "name" => name
        case "icon" => icon
        case "logs" =>  logs._1+","+logs._2
        case "friendsList" => friendList.foldLeft("")((s,u) => s + "," +u).drop(1)
        case "grailleLists" => grailleLists.foldLeft("")((s,g) => s + "," + g.hashCode()).drop(1)
      
    DataBase.replaceDataLine(pathFile,dataType,lineData)
  }
  
  override def hashCode(): Int = name.hashCode()

}
  object User{
  def retriveFromDb(userName : String) : User ={
    val USERS_FOLDER_PATH = "users/"
    val pathFile = USERS_FOLDER_PATH + userName.hashCode()+".txt" 
    val name = DataBase.getDataLinePrefixed(pathFile,"name").get(0)
    val icon = DataBase.getDataLinePrefixed(pathFile,"icon").get(0)
    val (logMail, logMdp) =
      val l = DataBase.getDataLinePrefixed(pathFile,"logs").get(0).split(",")
      (l(0),l(1))
    val friendsList = DataBase.getDataLinePrefixed(pathFile,"friendList").get(0).split(",").toList.filterNot(_.isBlank()).map(i => i.trim().toInt)
    val grailleLists = DataBase.getDataLinePrefixed(pathFile,"grailleLists").get(0).split(",").toList.filterNot(_.isBlank()).map(i => i.trim().toInt)
    new User(name,icon,(logMail,logMdp), friendsList, grailleLists)
  }

}
