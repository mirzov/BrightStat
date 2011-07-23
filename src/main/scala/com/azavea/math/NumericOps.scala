package com.azavea.math

/**
 * @author Erik Osheim
 */

/**
 * NumericOps adds things like inline operators to A. It's intended to
 * be used as an implicit decorator like so:
 *
 *   def foo[A:Numeric](a:A, b:A) = a + b
 *      (this is translated into) = new NumericOps(a).+(b)
 */
final class NumericOps[@specialized(Int,Long,Float,Double) A:Numeric](val lhs:A) {
  val n = implicitly[Numeric[A]]

  def abs() = n.abs(lhs)
  def compare(rhs:A) = n.compare(lhs, rhs)
  def equiv(rhs:A) = n.equiv(lhs, rhs)
  def max(rhs:A) = n.max(lhs, rhs)
  def min(rhs:A) = n.min(lhs, rhs)
  def unary_-() = n.negate(lhs)
  def signum() = n.signum(lhs)

  def <=>(rhs:A) = n.compare(lhs, rhs)
  def /(rhs:A) = n.div(lhs, rhs)
  def ===(rhs:A) = n.equiv(lhs, rhs)
  def !==(rhs:A) = !n.equiv(lhs, rhs)
  def >(rhs:A) = n.gt(lhs, rhs)
  def >=(rhs:A) = n.gteq(lhs, rhs)
  def <(rhs:A) = n.lt(lhs, rhs)
  def <=(rhs:A) = n.lteq(lhs, rhs)
  def -(rhs:A) = n.minus(lhs, rhs)
  def %(rhs:A) = n.mod(lhs, rhs)
  def +(rhs:A) = n.plus(lhs, rhs)
  def *(rhs:A) = n.times(lhs, rhs)
  def **(rhs:A) = n.pow(lhs, rhs)

  def <=>~[B:ConvertableFrom](rhs:B) = n.compare(lhs, n.fromType(rhs))
  def ===~[B:ConvertableFrom](rhs:B) = n.equiv(lhs, n.fromType(rhs))
  def !==~[B:ConvertableFrom](rhs:B) = !n.equiv(lhs, n.fromType(rhs))
  def max_[B:ConvertableFrom](rhs:B) = n.max(lhs, n.fromType(rhs))
  def min_[B:ConvertableFrom](rhs:B) = n.min(lhs, n.fromType(rhs))

  def /~[B:ConvertableFrom](rhs:B) = n.div(lhs, n.fromType(rhs))
  def >~[B:ConvertableFrom](rhs:B) = n.gt(lhs, n.fromType(rhs))
  def >=~[B:ConvertableFrom](rhs:B) = n.gteq(lhs, n.fromType(rhs))
  def <~[B:ConvertableFrom](rhs:B) = n.lt(lhs, n.fromType(rhs))
  def <=~[B:ConvertableFrom](rhs:B) = n.lteq(lhs, n.fromType(rhs))
  def -~[B:ConvertableFrom](rhs:B) = n.minus(lhs, n.fromType(rhs))
  def %~[B:ConvertableFrom](rhs:B) = n.mod(lhs, n.fromType(rhs))
  def +~[B:ConvertableFrom](rhs:B) = n.plus(lhs, n.fromType(rhs))
  def *~[B:ConvertableFrom](rhs:B) = n.times(lhs, n.fromType(rhs))
  def **~[B:ConvertableFrom](rhs:B) = n.pow(lhs, n.fromType(rhs))

  def toInt() = n.toInt(lhs)
  def toLong() = n.toLong(lhs)
  def toFloat() = n.toFloat(lhs)
  def toDouble() = n.toDouble(lhs)

  override def toString() = n.toString(lhs)
}


/**
 * IntOps, LongOps and friends provide the same tilde operators as NumericOps
 * (such as +~, -~, *~, etc) for the number types we're interested in.
 *
 * Using these, we use these operators with literals, number types, and
 * generic types. For instance:
 *
 *   def foo[A:Numeric](a:A, b:Int) = (a *~ b) +~ 1
 *
 * This isn't possible with the "normal" operators due to the fact that
 * Int.+ is already defined (as well as the fact that StringOps uses +).
 */

final class IntOps(val lhs:Int) {
  def <=>~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.compare(n.fromInt(lhs), rhs)
  def ===~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.equiv(n.fromInt(lhs), rhs)
  def !==~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = !n.equiv(n.fromInt(lhs), rhs)
  def max_[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.max(n.fromInt(lhs), rhs)
  def min_[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.min(n.fromInt(lhs), rhs)
  def /~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.div(n.fromInt(lhs), rhs)
  def >~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.gt(n.fromInt(lhs), rhs)
  def >=~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.gteq(n.fromInt(lhs), rhs)
  def <~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.lt(n.fromInt(lhs), rhs)
  def <=~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.lteq(n.fromInt(lhs), rhs)
  def -~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.minus(n.fromInt(lhs), rhs)
  def %~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.mod(n.fromInt(lhs), rhs)
  def +~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.plus(n.fromInt(lhs), rhs)
  def *~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.times(n.fromInt(lhs), rhs)
  def **~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.pow(n.fromInt(lhs), rhs)
}


final class LongOps(val lhs:Long) {
  def <=>~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.compare(n.fromLong(lhs), rhs)
  def ===~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.equiv(n.fromLong(lhs), rhs)
  def !==~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = !n.equiv(n.fromLong(lhs), rhs)
  def max_[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.max(n.fromLong(lhs), rhs)
  def min_[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.min(n.fromLong(lhs), rhs)
  def /~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.div(n.fromLong(lhs), rhs)
  def >~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.gt(n.fromLong(lhs), rhs)
  def >=~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.gteq(n.fromLong(lhs), rhs)
  def <~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.lt(n.fromLong(lhs), rhs)
  def <=~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.lteq(n.fromLong(lhs), rhs)
  def -~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.minus(n.fromLong(lhs), rhs)
  def %~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.mod(n.fromLong(lhs), rhs)
  def +~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.plus(n.fromLong(lhs), rhs)
  def *~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.times(n.fromLong(lhs), rhs)
  def **~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.pow(n.fromLong(lhs), rhs)
}


final class FloatOps(val lhs:Float) {
  def <=>~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.compare(n.fromFloat(lhs), rhs)
  def ===~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.equiv(n.fromFloat(lhs), rhs)
  def !==~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = !n.equiv(n.fromFloat(lhs), rhs)
  def max_[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.max(n.fromFloat(lhs), rhs)
  def min_[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.min(n.fromFloat(lhs), rhs)
  def /~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.div(n.fromFloat(lhs), rhs)
  def >~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.gt(n.fromFloat(lhs), rhs)
  def >=~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.gteq(n.fromFloat(lhs), rhs)
  def <~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.lt(n.fromFloat(lhs), rhs)
  def <=~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.lteq(n.fromFloat(lhs), rhs)
  def -~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.minus(n.fromFloat(lhs), rhs)
  def %~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.mod(n.fromFloat(lhs), rhs)
  def +~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.plus(n.fromFloat(lhs), rhs)
  def *~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.times(n.fromFloat(lhs), rhs)
  def **~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.pow(n.fromFloat(lhs), rhs)
}


final class DoubleOps(val lhs:Double) {
  def <=>~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.compare(n.fromDouble(lhs), rhs)
  def ===~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.equiv(n.fromDouble(lhs), rhs)
  def !==~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = !n.equiv(n.fromDouble(lhs), rhs)
  def max_[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.max(n.fromDouble(lhs), rhs)
  def min_[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.min(n.fromDouble(lhs), rhs)
  def /~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.div(n.fromDouble(lhs), rhs)
  def >~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.gt(n.fromDouble(lhs), rhs)
  def >=~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.gteq(n.fromDouble(lhs), rhs)
  def <~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.lt(n.fromDouble(lhs), rhs)
  def <=~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.lteq(n.fromDouble(lhs), rhs)
  def -~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.minus(n.fromDouble(lhs), rhs)
  def %~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.mod(n.fromDouble(lhs), rhs)
  def +~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.plus(n.fromDouble(lhs), rhs)
  def *~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.times(n.fromDouble(lhs), rhs)
  def **~[@specialized(Int,Long,Float,Double) A](rhs:A)(implicit n:Numeric[A]) = n.pow(n.fromDouble(lhs), rhs)
}
