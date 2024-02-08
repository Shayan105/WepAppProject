package tictactoe
class Restaurant(var name : String,var description : String,var rank : Int,var address: String) {
  override def toString(): String = name+"#"+description+"#"+rank+"#"+address
  override def equals(x: Any): Boolean =
    val o = x.asInstanceOf[Restaurant]
    o.name.trim() == this.name.trim() && o.description.trim() == this.description.trim()

 
}