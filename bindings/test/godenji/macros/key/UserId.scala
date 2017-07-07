package godenji.macros
package key

case class UserId(value: Id.Value) extends AnyVal with Id.Default

object UserId
  extends Zero { val zero = apply(UserId(_)) }
