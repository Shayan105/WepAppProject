package tictactoe

import scala.collection.immutable.Queue
import scala.util.{Failure, Success, Try}

import cs214.webapp.*
import cs214.webapp.exceptions.*
import cs214.webapp.server.WebServer
import cs214.webapp.messages.Action
import TicTacToeEvent.*
import TicTacToeView.*
import java.io.File
import java.io.PrintWriter
import javax.xml.crypto.Data
import tictactoe.GrailleList.retrieveFromDb
import tictactoe.AppActions.* 


object TicTacToeStateMachine extends cs214.webapp.StateMachine[TicTacToeEvent, TicTacToeState, TicTacToeView]:

  val name: String = "tictactoe"
  val wire = TicTacToeWire
  var x = 1;
  var user : User = null



  override def init(clients: Seq[UserId]): TicTacToeState =
    val logs = clients(0).split(";")
    val userName = logs(0)
    val password = logs(1)
    
    user = new User(userName,"iconBase",(userName,password),Nil,Nil)
    user.create()
    println("User: "+user.name+" logged in")
    tictactoeStateClass(clients, dashBoardPage_Act)
    

  // Failures in the Try must hold instances of AppException
  // (from Exceptions.scala under lib/shared/)
  override def transition(state: TicTacToeState)(uid: UserId, event: TicTacToeEvent): Try[Seq[Action[TicTacToeState]]] =
   Try{
    val file = "myFile.txt"
    
  

    event match
      case CreateUser(userName, password) =>
        println("user Created : "+ userName)
        val user : User = new User(userName,"iconBase",(userName,password),Nil,Nil)
        user.create()

        List(Action.Render(tictactoeStateClass(state.players,AppActions.dashBoardPage_Act)))


      case _ => throw new IllegalArgumentException("Illegal Event")

  }



    
  override def project(state: TicTacToeState)(uid: UserId): TicTacToeView =
    val username = user.name
    val graillelistsView : List[GrailleListView] = user.getGrailleLists()
      .map(GrailleList.retrieveFromDb(_)) 
      .map(
        gl => GrailleListView(
        gl.name,
        gl.collaborators.map(User.retriveFromDb(_).name),
        gl.getRestaurants().map(r => RestaurantView(r.name,r.description,r.rank,r.address))
        ))
    val friendList : List[String] = user.getFriendList().map(User.retriveFromDb(_).name) 
    val userPayload = UserPayload("iconBase",friendList,graillelistsView)


    //Should be matching here 
    dashBoardPage(username,userPayload)

    

// Server registration magic
class register:
  WebServer.register(TicTacToeStateMachine)

@main def l()=

  //DataBase.findLinePrefixed("myFile.txt","shayan",false).get.foreach(println)
  //DataBase.getDataLinePrefixed(file,"age").get.foreach(println)
  //DataBase.replaceDataLine(file,"name","laplace")
  //DataBase.replaceDataLine(file,"sexe","nonbin")
  println("restoTEst")
  val r1 = new Restaurant("Kebab","aimez manger des kebab",2,"annoncillades")
  val r2 = new Restaurant("Glace","vive les glaces ",2,"paris")
  val t = new User("thitie","iconThitie",("thitie@hotmail","mpdpThitie"),Nil,Nil)
  t.create()
  val s = new User("shayan","iconThitie",("thitie@hotmail","mpdpThitie"),Nil,Nil)
  s.create()
  val v = new User("valentin","iconThitie",("thitie@hotmail","mpdpThitie"),Nil,Nil)
  v.create()


  val g = new GrailleList("magragra", Nil, Nil)
  g.create()
  //g.addRestaurant(r1)
  g.removeRestaurant(r1)
 // g.addRestaurant(r2)

  //s.removeFriend(v.hashCode())
  //t.removeFriend(v.hashCode())
  t.addFriend(s.hashCode())
  t.addFriend(v.hashCode())
  //g.removeCollaborator(t.hashCode())
  //g.addCollaborator(v.hashCode())
  //g.addCollaborator(t.hashCode())
  g.addCollaborator(s.hashCode())
  s.getGrailleLists().foreach( l => GrailleList.retrieveFromDb(l).getRestaurants().foreach( r => println(r.name)))





