package godenji.macros
package key

abstract class Zero {
  def apply[T <: Id.Default](fn: T#Underlying => T) = fn(Id.zero)
}