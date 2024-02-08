package tictactoe

import tictactoe.User.retriveFromDb

class GrailleList(var name : String, var collaborators : List[Int], var restaurantList : List[Restaurant]) {
  private val GRAILLE_LISTS_FOLDER_PATH = "graille_lists/"

  private def pathFile: String = GRAILLE_LISTS_FOLDER_PATH + this.hashCode() + ".txt"
  override def hashCode(): Int = name.trim().hashCode()


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
    else
        //println("Retrieing Graille List")
        refresh()



   }
   /**
    * Takes values from the db to update the object, must be done before giving any information
   */
   def refresh() ={
        val retrieved = GrailleList.retrieveFromDb(this.hashCode())
        name = retrieved.name
        collaborators = retrieved.collaborators
        restaurantList = retrieved.restaurantList
   }

    def addCollaborator(user: Int): Unit = {
        refresh()
        if (!collaborators.contains(user.hashCode())) {
        collaborators = user :: collaborators
        update("collaborators")
        val otherUser = User.retriveFromDb(user)
        otherUser.grailleLists = this.hashCode() :: otherUser.grailleLists
        otherUser.update("grailleLists")
        }
    }

    def removeCollaborator(user: Int): Unit = {
        if (collaborators.contains(user)) {
        collaborators = collaborators.filterNot(_ == user)
        update("collaborators")
        val otherUser = User.retriveFromDb(user)
        otherUser.grailleLists = otherUser.grailleLists.filterNot(_ == this.hashCode())
        otherUser.update("grailleLists")
        }
    }

    def addRestaurant(restaurant: Restaurant): Unit = {
        if !restaurantList.contains(restaurant) then 
            restaurantList = restaurant :: restaurantList
            update("restaurantList")
        else print ("Already present")
        
        }
    

    def removeRestaurant(restaurant: Restaurant): Unit = {
        if (restaurantList.contains(restaurant)) {
        // Handle potential dependencies or relationships before removal
        restaurantList = restaurantList.filterNot(_.equals(restaurant) )
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

    def getRestaurants() :List[Restaurant]=
        refresh()
        print(restaurantList)
        restaurantList
        
    def getCollaborators() : List[Int] = 
        refresh()
        collaborators




}

object GrailleList{

    def retrieveFromDb(id :Int):GrailleList={

        val GRAILLE_LISTS_FOLDER_PATH = "graille_lists/"
        def pathFile: String = GRAILLE_LISTS_FOLDER_PATH + id + ".txt"
        val name = DataBase.getDataLinePrefixed(pathFile,"name").get(0).trim()
        val collaborators = DataBase.getDataLinePrefixed(pathFile,"collaborators").get(0).split(",").map(_.split(",").filterNot(_.equals("")).map(_.trim().toInt)).flatten.toList
        val restaurantsList = DataBase.getDataLinePrefixed(pathFile,"restaurantList").get(0).split(",").map(_.split("#")).map(
            arr => new Restaurant(arr(0),arr(1),arr(2).toInt,arr(3))
        ).toList
        new GrailleList(name,collaborators,restaurantsList)
    }
}
