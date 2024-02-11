package memory

import scala.util.{Failure, Success, Try}

import cs214.webapp.*
import cs214.webapp.wires.*
import cs214.webapp.exceptions.DecodingException

object MemoryWire extends AppWire[MemoryEvent, MemoryView]:
  import MemoryEvent.*
  import MemoryView.*
  import ujson.*

  override object eventFormat extends WireFormat[MemoryEvent]:
    override def encode(event: MemoryEvent): Value = event match
      case Toggle(cardId) => Arr(Str("Toggle"),Num(cardId))
      case FlipSelected => Arr(Str("FlipSelected"))

    override def decode(js: Value): Try[MemoryEvent] =Try{
      val array = js.arr
      array(0).str match
        case "Toggle" => Toggle(array(1).num.toInt)
        case "FlipSelected" => FlipSelected
        case _ => throw DecodingException("")
    }
    
  override object viewFormat extends WireFormat[MemoryView]:
  
    override def encode(v: MemoryView): Value =
      def encodeSateView =
        val stateView = v.stateView
        stateView match
          case StateView.Playing(phase, currentPlayer, board) => 
            val phaseEncoded = phase match
              case PhaseView.SelectingCards => "SelectingCards"
              case PhaseView.CardsSelected => "CardsSelected"
              case PhaseView.Waiting => "Waiting"
              case PhaseView.GoodMatch => "GoodMatch"
              case PhaseView.BadMatch => "BadMatch"

            val currentPlayerEncoded = Str(currentPlayer)
            val boardEncoded = Arr(board.map({cw => cw match
              case CardView.FaceDown => Arr(Str("FaceDown"))
              case CardView.Selected =>Arr(Str("Selected"))
              case CardView.FaceUp(card) =>Arr(Str("FaceUp"),Str(card))
              case CardView.AlreadyMatched(card) =>Arr(Str("AlreadyMatched"), Str(card))
            })*)

            Arr(Str("Playing"),phaseEncoded,currentPlayerEncoded,boardEncoded)

          case StateView.Finished(winnerIds) => Arr(Str("Finished"), Arr(winnerIds.map(Str(_))))

      def encodeScoreView =
        MapWire(StringWire,SeqWire(StringWire)).encode(v.alreadyMatched)
      Arr(encodeSateView,encodeScoreView)


    override def decode(js: Value): Try[MemoryView] =Try{
      val encodedStateView = js.arr(0)
      val encodedScoreView = js.arr(1)

      def decodeStateView = encodedStateView.arr(0).str match
        case "Playing" =>{
          val phaseDecoded = encodedStateView.arr(1).str match
            case "SelectingCards" => PhaseView.SelectingCards
            case "CardsSelected" => PhaseView.CardsSelected
            case "Waiting" => PhaseView.Waiting
            case "GoodMatch" => PhaseView.GoodMatch
            case "BadMatch" => PhaseView.BadMatch
            case _ => throw DecodingException("")
          val currentPlayerDecoded = encodedStateView.arr(2).str
          val boardDecoded = encodedStateView.arr(3).arr.toList.map { card =>
            card.arr(0).str match
              case "FaceDown" => CardView.FaceDown
              case "Selected" => CardView.Selected
              case "FaceUp" => CardView.FaceUp(card.arr(1).str)
              case "AlreadyMatched" =>CardView.AlreadyMatched(card.arr(1).str)
              case _ => throw DecodingException("")
          }
          StateView.Playing(phaseDecoded, currentPlayerDecoded, boardDecoded)
        }
        case "Finished" =>{
          val winnerIdsDecoded = encodedStateView.arr(1).arr.toList.map(_.str)
          StateView.Finished(winnerIdsDecoded.toSet)
        }
        case _ => throw DecodingException("")


      def decodeScoreView = MapWire(StringWire,SeqWire(StringWire)).decode(encodedScoreView) .get
      MemoryView(decodeStateView, decodeScoreView)



      
      }


    
      


