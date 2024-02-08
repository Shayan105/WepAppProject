package tictactoe

import javax.xml.crypto.Data
import scala.annotation.static
import java.sql.DatabaseMetaData
import tictactoe.User.retriveFromDb
import tictactoe.GrailleList.retrieveFromDb
class User(var name : String, icon : String,var logs : (String,String),var friendList : List[Int],var  grailleLists : List[Int] ) {
  private val USERS_FOLDER_PATH = "users/"
  private val pathFile = USERS_FOLDER_PATH + this.hashCode()+".txt" 
  //println(name + "gives path"+ pathFile+ "hash is "+ this.hashCode())

  /** creates all the files requirement for a new user if it doesent exist, does nothig if it already exist */

  def getFriendList() : List[Int]={
    val l = retriveFromDb(this.hashCode).friendList
    friendList = l 
    friendList
  }
  def getGrailleLists() : List[Int] ={
    refresh()
    grailleLists
  }

  def refresh()=
    val retrieved = User.retriveFromDb(this.hashCode())
    name = retrieved.name
    logs = retrieved.logs
    friendList = retrieved.friendList
    grailleLists = retrieved.grailleLists



  def create() = {
    val exists = DataBase.fileExists(pathFile)
    exists match
      case true =>
        //println("file at address "+pathFile+ " already exists...Retrieving" )
        refresh()


      case false =>
        //print("Noe exist")
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
  def addFriend( friend : Int)= {
    if (!friendList.contains(friend)) then
      //print("Not currentrly friends with "+ friend )
      friendList = friend::friendList
      val otherUser = User.retriveFromDb(friend)
      otherUser.friendList = this.hashCode() ::otherUser.friendList 
      //println("other user is:" + otherUser.name)
      otherUser.update("friendList")
      update("friendList")
    else println("already friends")
  }
  def addGrailleList( graillelists : GrailleList)= {
    grailleLists = graillelists.hashCode() :: grailleLists
    update("grailleLists")
  }
    def addGrailleList( graillelists : Int)= {
    grailleLists = graillelists :: grailleLists
    update("grailleLists")
  }
  def removeFriend(friend : Int) ={
   if (!friendList.contains(friend)) then
    print("Not currentrly friends with "+friend )
  else
    val otherUser = User.retriveFromDb(friend)
    otherUser.friendList = friendList.filter( x=>  x!=friend)
    otherUser.update("friendList")
    friendList = friendList.filter( x=>  x!=friend)
    update("friendList")
  

  }



  def update(dataType : String)={
    val lineData = 
      dataType match
        case "name" => name
        case "icon" => icon
        case "logs" =>  logs._1+","+logs._2
        case "friendList" => friendList.foldLeft("")((s,u) => s + "," +u).drop(1)
        case "grailleLists" => grailleLists.foldLeft("")((s,g) => s + "," + g.hashCode()).drop(1)

    println(this.name +"updating "+dataType+"with path "+ pathFile+ "where hashcode is "+ this.hashCode() )  
    DataBase.replaceDataLine(pathFile,dataType,lineData)
  }
  
  override def hashCode(): Int = name.hashCode()

}
  object User{
  def retriveFromDb(userId : Int) : User ={
    val USERS_FOLDER_PATH = "users/"
    val pathFile = USERS_FOLDER_PATH + userId+".txt" 
    val name = DataBase.getDataLinePrefixed(pathFile,"name").get(0).trim()
    val icon = DataBase.getDataLinePrefixed(pathFile,"icon").get(0)
    val (logMail, logMdp) =
      val l = DataBase.getDataLinePrefixed(pathFile,"logs").get(0).split(",")
      (l(0),l(1))
    val friendsList = DataBase.getDataLinePrefixed(pathFile,"friendList").get(0).split(",").toList.filterNot(_.isBlank()).map(i => i.trim().toInt)
    val grailleLists = DataBase.getDataLinePrefixed(pathFile,"grailleLists").get(0).split(",").toList.filterNot(_.isBlank()).map(i => i.trim().toInt)
    new User(name,icon,(logMail,logMdp), friendsList, grailleLists)
  }

}
