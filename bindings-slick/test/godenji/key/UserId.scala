package godenji.iso
package key

case class UserId(value: Id.Value) extends AnyVal with Id[Id.Value]

object UserId
  extends Zero { val zero = apply(UserId(_)) }
