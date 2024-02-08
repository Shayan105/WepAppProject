package tictactoe
class Restaurant(var name : String,var description : String,var rank : Int,var address: String) {
  override def toString(): String = name+"#"+description+"#"+rank+"#"+address
}