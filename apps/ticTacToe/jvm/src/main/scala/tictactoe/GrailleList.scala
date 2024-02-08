package tictactoe

import tictactoe.User.retriveFromDb

class GrailleList(var name : String, var collaborators : List[Int], var restaurantList : List[Restaurant]) {
  private val GRAILLE_LISTS_FOLDER_PATH = "graille_lists/"

  private def pathFile: String = GRAILLE_LISTS_FOLDER_PATH + name.hashCode() + ".txt"


  def create()={
   if (!DataBase.fileExists(pathFile)) {
      DataBase.createFile(pathFile)
      // Save initial data to file
      val lines = List(
        "name:" + name,
        "collaborators:" + collaborators.map(_.hashCode()).mkString(","),
        "restaurantList:" + restaurantList.map(_.toString).mkString(",")
      )
      DataBase.appender(pathFile)(true)(lines)}
    else GrailleList.retrieveFromDb(name.hashCode())}
    
    def addCollaborator(user: User): Unit = {
        if (!collaborators.contains(user.hashCode)) {
        collaborators = user.hashCode() :: collaborators
        update("collaborators")
        }
    }

    def removeCollaborator(user: User): Unit = {
        if (collaborators.contains(user)) {
        collaborators = collaborators.filterNot(_ == user.hashCode())
        update("collaborators")
        }
    }

    def addRestaurant(restaurant: Restaurant): Unit = {
        restaurantList = restaurant :: restaurantList
        update("restaurantList")
        
        }
    

    def removeRestaurant(restaurant: Restaurant): Unit = {
        if (restaurantList.contains(restaurant)) {
        // Handle potential dependencies or relationships before removal
        restaurantList = restaurantList.filterNot(_.hashCode() == restaurant.hashCode())
        update("restaurantList")
        }
    }

    def update(dataType: String): Unit = {
        val dataMap = Map(
        "name" -> name,
        "collaborators" -> collaborators.mkString(","),
        "restaurantList" -> restaurantList.map(_.toString).mkString(",")
        )
        println("to write:" + dataMap(dataType))
        DataBase.replaceDataLine(pathFile, dataType, dataMap(dataType))
    }

    def shareGrailleList(user: User): Unit = {
        // Implement sharing logic, considering access control and permissions
    }


}
object GrailleList{

    def retrieveFromDb(id :Int):GrailleList={

        val GRAILLE_LISTS_FOLDER_PATH = "graille_lists/"
        def pathFile: String = GRAILLE_LISTS_FOLDER_PATH + id + ".txt"
        val name = DataBase.getDataLinePrefixed(pathFile,"name").get(0)
        val collaborators = DataBase.getDataLinePrefixed(pathFile,"collaborators").get(0).split(",").map(_.split(",").map(_.trim().toInt)).flatten.toList
        val restaurantsList = DataBase.getDataLinePrefixed(pathFile,"restaurantList").get.map(_.split("#")).map(
            arr => new Restaurant(arr(0),arr(1),arr(2).toInt,arr(3))
        )
        new GrailleList(name,collaborators,restaurantsList)
    }
}
