package se.lu.chemphys.sms.brightstat.ui

class MoleculeStatus(val x: Int, val y: Int, val save: Boolean ){
  
  def this(xy: (Int, Int)) = this(xy._1, xy._2, true)
  
}

class NoMoleculesToShow extends MoleculesToShow

class SomeMoleculesToShow(coordinates: Seq[(Int, Int)]) extends MoleculesToShow{
  coords = coordinates.toIndexedSeq
  saveStates = Array.fill(coords.length)(true)
}

trait MoleculesToShow extends IndexedSeq[MoleculeStatus]{
  
  protected var coords: IndexedSeq[(Int, Int)] = IndexedSeq()
  protected var saveStates: Array[Boolean] = Array()
  
  override def apply(i: Int) = new MoleculeStatus(coords(i)._1, coords(i)._2, saveStates(i))
  override def length = saveStates.length
  
  def getMoleculeInRange(x: Int, y: Int, imageRad: Float): Option[Int] = {
    val dist2max = imageRad * imageRad
    coords.indexWhere{c: (Int, Int) => dist2(x - c._1, y - c._2) < dist2max} match {
      case -1 => None
      case x => Some(x)
    }
  }
  
  private def dist2(dx: Int, dy: Int): Float = dx * dx + dy * dy
  
  def update(i: Int, value: Boolean): Unit = {
    saveStates(i) = value
  }
}