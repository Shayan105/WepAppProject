package tictactoe
//import upickle.default._
import ujson.*
import scala.util.{Failure, Success, Try}
import cs214.webapp.wires.*
import cs214.webapp.exceptions.DecodingException
import cs214.webapp.{AppWire, WireFormat, UserId}

object TicTacToeWire extends AppWire[TicTacToeEvent, TicTacToeView]:
  import TicTacToeEvent.*
  import TicTacToeView.*
  import upickle.default._
  import upickle.default.{ReadWriter => RW, macroRW}

  override object eventFormat extends WireFormat[TicTacToeEvent]:
    override def encode(t: TicTacToeEvent): Value =

    t match
      case Move(x,y) => Arr(Str("MOVE"),Obj("x" -> Num(x), "y" -> Num(y)))
      case CreateUser(userName, password) => Arr(Str("CREATE_USER"),Str(userName), Str(password))

    override def decode(json: Value): Try[TicTacToeEvent] =
      Try{
        val value = json.arr
        value(0).str match
          case "MOVE" =>  
            val xVal = value(1).obj("x").num.toInt
            val yVal = value(1).obj("y").num.toInt
            Move(xVal,yVal)

          case "CREATE_USER" =>
            val username = value(1).str
            val password = value(2).str
            println("wire:"+ username+ " " + password)
            CreateUser(username,password)


 
      }

  override object viewFormat extends WireFormat[TicTacToeView]:
    
    def encode(t: TicTacToeView): Value = t  match
      case dashBoardPage(username : String, userPayload : UserPayload ) =>
        Arr(Str("dashBoardPage"),Str(upickle.default.write(username)), Str(upickle.default.write(userPayload)))

    
    
      

    def decode(json: Value): Try[TicTacToeView] = Try{
      val tag = json.arr(0).str
      tag match
        case "dashBoardPage" =>
          val username = upickle.default.read[String](json.arr(1).str)
          val userPayload = upickle.default.read[UserPayload](json.arr(2).str)
          dashBoardPage(username , userPayload)

    }

