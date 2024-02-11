
package tictactoe

import cs214.webapp.UserId
import scala.util.{Failure, Success, Try}
import upickle.default.{ReadWriter => RW, macroRW}
/** Stores all information about the current game. */
 // Change this type to hold actual information (use an enum, class, …)
type TicTacToeState = tictactoeStateClass

case class tictactoeStateClass(players : Seq[UserId], currentAction : AppActions)

/** There is only one event in tic-tac-toe: clicking a cell. */
enum TicTacToeEvent:
  /** User clicked cell (x, y) */
  case Move(x: Int, y: Int)
  case CreateUser(userName: String, password : String)

/** Client views reflect the state of the game: playing or finished. */
enum TicTacToeView:
  case dashBoardPage(userName : String, userPayload : UserPayload )



// Change this class definition to store board states.
case class UserPayload(icon : String , friendList : List[String] , grailleListsView : List[GrailleListView])
case class GrailleListView(name : String, collaborators : List[String], restaurantList : List[RestaurantView])
case class RestaurantView(var name : String,var description : String,var rank : Int,var address: String)

object UserPayload {implicit val rw: RW[UserPayload] = macroRW}
object GrailleListView {implicit val rw: RW[GrailleListView] = macroRW}
object RestaurantView {implicit val rw: RW[RestaurantView] = macroRW}

case class Board(state :  List[List[Option[UserId]]]):
  /** Get the value in the cell at (r, c). */
 // var state : TicTacToeState = List.fill(3)(List.fill(3)(None))

  def apply(r: Int, c: Int): Option[UserId] =
    require( r >= 0 && r<=2 && c >= 0 && c<=2 ) // Add an appropriate precondition
    state(r)(c)
    

