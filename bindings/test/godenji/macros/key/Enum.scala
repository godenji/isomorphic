package godenji.macros
package key

case class 
  Enum(value: String) extends AnyVal with Id[String] {
	
	override def toString() = value.toLowerCase
}

object Enum {val zero = Enum("FOO")}
