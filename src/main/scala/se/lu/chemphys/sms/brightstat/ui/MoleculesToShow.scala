package se.lu.chemphys.sms.brightstat.ui

class MoleculeStatus(val x: Int, val y: Int, val selected: Boolean ){
  
  def this(xy: (Int, Int)) = this(xy._1, xy._2, true)
  
}

class NoMoleculesToShow extends MoleculesToShow{
  
  override def replaceCoordinates(coordinates: Seq[(Int, Int)]) = new SomeMoleculesToShow(coordinates)
  
}

class SomeMoleculesToShow(coordinates: Seq[(Int, Int)], states: Seq[Boolean]) extends MoleculesToShow{
  
  coords = coordinates.toIndexedSeq
  saveStates = states.toArray

  def this(coordinates: Seq[(Int, Int)]) = this(coordinates, Array.fill(coordinates.length)(true))
  
  override def replaceCoordinates(newCoords: Seq[(Int, Int)]) = {
    val indCoords = newCoords.toIndexedSeq
    if(indCoords.length != coords.length) new SomeMoleculesToShow(indCoords);
    else new SomeMoleculesToShow(indCoords, saveStates)
  }
}

trait MoleculesToShow extends IndexedSeq[MoleculeStatus]{
  
  def replaceCoordinates(coordinates: Seq[(Int, Int)]): MoleculesToShow
  
  protected var coords: IndexedSeq[(Int, Int)] = IndexedSeq()
  protected var saveStates: Array[Boolean] = Array()
  
  override def apply(i: Int) = new MoleculeStatus(coords(i)._1, coords(i)._2, saveStates(i))
  override def length = coords.length
  
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
  
  def deselectAll() {
    saveStates = Array.fill(coords.length)(false)
  }
  
  def selectAll() {
    saveStates = Array.fill(coords.length)(true)
  }
  
  def keepOnlySelected() {
    coords = (0 to coords.length - 1).toIndexedSeq.filter(saveStates(_)).map(coords(_))
    selectAll
  }
}