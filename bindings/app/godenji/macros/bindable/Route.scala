package godenji.macros
package bindable

import play.api.mvc.{PathBindable, QueryStringBindable}

object Route {
  import isomorphism._
  /**
   * materialize a value class path bindable
   */
  implicit final def pathBindValueClass[T <: MappedToBase](implicit pathBind: PathBindable[T#Underlying], iso: Isomorphism[T]): PathBindable[T] = new PathBindable[T] {
    override def bind(key: String, value: String): String Either T =
      pathBind.bind(key, value).fold(
        Left(_), i => Right(iso.comap(i))
      )

    override def unbind(key: String, id: T): String = {
      // `pathBind.unbind(key, iso.map(id) )`
      // we ignore above T => T#Underlying conversion since MappedTo
      // defines a default toString that returns Underlying as String;
      // if a subclass defines its own toString it would *not* be applied
      // since `iso.map(id)` converts to underlying primitive value
      // and then Play's built-in binders apply String conversion, thus
      // omitting the custom toString operation.
      // solution is simple: call toString directly on T.
      id.toString
    }
  }

  /**
   * materialize a value class querystring bindable
   */
  implicit final def qsBindValueClass[T <: MappedToBase](implicit qsBind: QueryStringBindable[T#Underlying], iso: Isomorphism[T]): QueryStringBindable[T] = new QueryStringBindable[T] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[String Either T] =
      qsBind.bind(key, params).map(_.fold(
        Left(_), i => Right(iso.comap(i))
      ))

    override def unbind(key: String, id: T): String = {
      qsBind.unbind(key, iso.map(id))
    }
  }
}