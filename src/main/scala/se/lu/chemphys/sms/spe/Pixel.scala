package se.lu.chemphys.sms.spe

case class Pixel[T](x:Int, y:Int, I:T)(implicit num: Numeric[T]) {

}