package godenji.iso
package key

/**
 * identifier base trait for value classes
 */
trait Id[T] extends Any with MappedTo[T] {
  def empty: Boolean = value == Id.zero
}
object Id {
  private val (intZero, longZero, uuidZero) = (
    0, 0L, new java.util.UUID(0L, 0L)
  )

  /** default Id value's type */
  type Value = Long

  /** default Id */
  type Default = Id[Value]

  /** default zero value */
  val zero: Value = longZero

  /** cast String id to Value (e.g. from HTTP cookie or request body) */
  def toValue(id: String): Value = id.toLong
}
