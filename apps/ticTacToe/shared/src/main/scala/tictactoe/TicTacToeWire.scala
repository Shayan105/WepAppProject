package tictactoe

import ujson.*
import scala.util.{Failure, Success, Try}

import cs214.webapp.wires.*
import cs214.webapp.exceptions.DecodingException
import cs214.webapp.{AppWire, WireFormat, UserId}

object TicTacToeWire extends AppWire[TicTacToeEvent, TicTacToeView]:
  import TicTacToeEvent.*
  import TicTacToeView.*

  override object eventFormat extends WireFormat[TicTacToeEvent]:
    override def encode(t: TicTacToeEvent): Value =
    t match
      case Move(x,y) => Arr(Str("MOVE"),Obj("x" -> Num(x), "y" -> Num(y)))

    override def decode(json: Value): Try[TicTacToeEvent] =
      Try{
        val value = json.arr
        val xVal = value(1).obj("x").num.toInt
        val yVal = value(1).obj("y").num.toInt
        Move(xVal,yVal)



 
      }

  override object viewFormat extends WireFormat[TicTacToeView]:

    def encode(t: TicTacToeView): Value = t match
      case Playing(board, yourTurn) =>{
        val listIndexed  = (
          for
            (row, indRow) <- board.state.zipWithIndex
            (el , indCol) <- row.zipWithIndex
          yield (Str(el.getOrElse("None"))))

        Arr(Str("VIEW_PLAYING"),if yourTurn then True else False, Arr(listIndexed: _*))}

      case Finished(winnerUser) => Arr(Str("VIEW_FINISHED"),Str(winnerUser.getOrElse("None")))
      



    def decode(json: Value): Try[TicTacToeView] = Try{
      val data = json.arr
      if data(0).str == "VIEW_PLAYING" then 
        val boardState = data(2).arr.map(e => if e.str == "None" then None else Some(e.str)).toList.grouped(3).toList
        val yourTurn = data(1) == True
        Playing(Board(boardState), yourTurn)  
      
      else Finished( if data(1).str == "None" then None else Some(data(1).str))

    }
