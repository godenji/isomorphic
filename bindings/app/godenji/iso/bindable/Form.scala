package godenji.iso
package bindable

import play.api.data.format.{Formatter, Formats}, Formats._
import play.api.data.FormError

object Form {
  /**
   * materialize a value class form bindable
   */
  implicit final def formBindValueClass[T <: MappedToBase]
    (implicit formBind: Formatter[T#Underlying], iso: Isomorphism[T]): Formatter[T] = new Formatter[T] {

    override def bind(key: String, data: Map[String, String]): Seq[FormError] Either T =
      formBind.bind(key, data).fold(
        Left(_), s => Right(iso.comap(s))
      )

    override def unbind(key: String, id: T): Map[String, String] =
      formBind.unbind(key, iso.map(id))
  }

}
