package tictactoe

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import org.scalajs.dom.html.Input
import org.scalajs.dom
import scalatags.JsDom.all.*

import cs214.webapp.*
import cs214.webapp.client.*

object TicTacToeClientApp extends WSClientApp:
  def name: String = "tictactoe"

  def init(userId: UserId, sendMessage: ujson.Value => Unit, target: dom.Element): ClientAppInstance =
    TicTacToeClientAppInstance(userId, sendMessage, target)

class TicTacToeClientAppInstance(userId: UserId, sendMessage: ujson.Value => Unit, target: dom.Element)
    extends StateMachineClientAppInstance[TicTacToeEvent, TicTacToeView](userId, sendMessage, target):
  def name: String = "tictactoe"

  val wire = TicTacToeWire

  private def sendMove(r: Int, c: Int) =
    sendEvent(TicTacToeEvent.Move(r, c))

  def render(userId: UserId, view: TicTacToeView): Frag =
    frag(
      h2(b("Tic-tac-toe: "), "Be the first to draw the line!"),
      renderView(userId, view)
    )

  def renderView(userId: UserId, view: TicTacToeView): Frag = view match
    case TicTacToeView.dashBoardPage(username : String, userPayload : UserPayload)=>div(
      div(
        b("nom:"),
        username
      ),
      div(
        b("Friends:"),
        for f <- userPayload.friendList
        yield div(f)
      ),
      div(
        b("Restaurant to See"),
        for
          l <- userPayload.grailleListsView
          r <- l.restaurantList
        yield div(r.name + " with :"+ l.collaborators.reduce((a,b) => a+""+b))

      ))

      
      //WebClient.navigateTo(HomePage)
/*       div(
        input(`type` :="text", id:="userName", required:=true, size:="10", placeholder:="UserName"),
        p(
          input(`type` :="text", id:="userPassword", required:=true, size:="10", placeholder:="Password"),
          button("salut", onclick :=  (() =>
            val username = getElementById[Input]("userName").value
            val password = getElementById[Input]("userPassword").value
            println("command sent ("+ username +" "+ password +")")
            sendEvent(TicTacToeEvent.CreateUser(username,password))
            ))

         // img(src := "sf",onclick := sendEvent(TicTacToeEvent.CreateUser(getElementById[Input]("userName").value,getElementById[Input]("userPassword").value)), "Log In")
        )
      ) */


      

// Scala.js magic to register our application from this file
@JSExportAll
object TicTacToeRegistration:
  @JSExportTopLevel("TicTacToeExport")
  val registration = WebClient.register(TicTacToeClientApp)
