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


object TicTacToeStateMachine extends cs214.webapp.StateMachine[TicTacToeEvent, TicTacToeState, TicTacToeView]:

  val name: String = "tictactoe"
  val wire = TicTacToeWire
  var x = 1;


  def write(message: String): Unit = {
  val file = new File("myFile.txt")
  if (!file.exists()) {
    file.createNewFile()
    println("created")
  }
  println("writting")
  val pw = new PrintWriter(file)
  pw.close()}



  override def init(clients: Seq[UserId]): TicTacToeState =
    tictactoeStateClass(clients,clients(1),List.fill(3)(List.fill(3)(None)))
    

  // Failures in the Try must hold instances of AppException
  // (from Exceptions.scala under lib/shared/)
  override def transition(state: TicTacToeState)(uid: UserId, event: TicTacToeEvent): Try[Seq[Action[TicTacToeState]]] =
   Try{
    val file = "myFile.txt"
    
    DataBase.replaceDataLine(file,"age","100"+x)
    x = x+1

    event match
      case TicTacToeEvent.Move(y,x) => {
        if !( y >= 0 && y<=2 && x >= 0 && x<=2 ) then throw IllegalMoveException("Out of bounds")
        else{ 
 
        state.GSA(y)(x) match
          case None => {
            val newStateRow = state.GSA(y).updated(x,Some(uid))
            val newState = state.GSA.updated(y,newStateRow)
            val clients = state.players
            val lastClient = uid
            if state.lastClient != lastClient then 
              List(Action.Render(tictactoeStateClass(clients,uid,newState)))
            else throw NotYourTurnException() 
                    }
        
          case _ => throw IllegalMoveException("Place already taken")
  
        }
          
       

      }
  }



    
  override def project(state: TicTacToeState)(uid: UserId): TicTacToeView =
    val board = Board(state.GSA)
    val lastClient = state.lastClient 
    val isFinished= {
   
    
    val diagDone=(
      for
        x<-(0 until 3 )
      yield (state.GSA(x)(x))).forall( _ == Some(lastClient))||{
        (for
          x<-(0 until 3 )
        yield (state.GSA(x)(2-x))).forall( _ == Some(lastClient))}

    val verticalsDone=(
      for
        x <- (0 until 3)
      yield (state.GSA(0)(x)== Some(lastClient) && state.GSA(1)(x)== Some(lastClient) && state.GSA(2)(x)== Some(lastClient))).contains(true)
    
    val horizontalDone=(
      for
        x <- (0 until 3)
      yield (state.GSA(x)(0)== Some(lastClient) && state.GSA(x)(1)== Some(lastClient) && state.GSA(x)(2)== Some(lastClient))).contains(true)
   // println("diag:"+ diagDone)
 //   println("Hor:"+ horizontalDone)
 //   println("Vert:"+ verticalsDone)
    horizontalDone || verticalsDone || diagDone
    }

    val inATie = state.GSA.flatMap(_.toList).forall(_ != None) && !isFinished
    if inATie then Finished(None) else if isFinished then Finished(Some(lastClient)) else Playing(board,state.lastClient != uid)


    

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





