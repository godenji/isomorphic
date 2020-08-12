/*
Copyright 2011-2016 Typesafe, Inc.

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * this is a modified version of Slick's value class isomorphism:
 * @see https://github.com/slick/slick/blob/648184c7cb710563d07b859891ed7fe46d06849d/slick/src/main/scala/slick/lifted/MappedTo.scala
 */
package godenji.macros
package isomorphism

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import scala.util.control.NonFatal

/**
 * convert to/from a value class and its underlying
 * primitive type
 */
final class Isomorphism[T <: MappedToBase](
  val map: T => T#Underlying, val comap: T#Underlying => T
)

/**
 * trait that your value classes must extend
 * (provides concrete primitive type for macro materializer)
 *
 * {{{case class FooId(id: Long) extends AnyVal with MappedTo[Long]}}}
 */
trait MappedTo[T] extends Any with MappedToBase {
  final type Underlying = T
  def value: T
  override def toString() = s"$value"
}

/**
 * base type used to materialize value class isomorphism
 */
trait MappedToBase extends Any {
  type Underlying
  def value: Underlying
}

/**
 * macro that materializes the isomorphism
 */
object MappedToBase {
  implicit def isomorphicToIsomorphism[T <: MappedToBase]: Isomorphism[T] = macro isomorphicToIsomorphismMacro[T]

  def isomorphicToIsomorphismMacro[T <: MappedToBase](c: Context)(implicit e: c.WeakTypeTag[T]): c.Expr[Isomorphism[T]] = {

    import c.universe._
    if (!(e.tpe <:< c.typeOf[MappedToBase])) c.abort(
      c.enclosingPosition,
      "Work-around for illegal macro-invocation; see SI-8351"
    )
    implicit val eutag = c.TypeTag[T#Underlying](
      e.tpe.member(TypeName("Underlying")).typeSignatureIn(e.tpe)
    )
    val cons = c.Expr[T#Underlying => T](Function(
      List(ValDef(
        Modifiers(Flag.PARAM), TermName("v"), TypeTree(), EmptyTree
      )),
      Apply(
        Select(New(TypeTree(e.tpe)), termNames.CONSTRUCTOR),
        List(Ident(TermName("v")))
      )
    ))
    val res = reify {
      new Isomorphism[T](_.value, cons.splice)
    }
    try c.typecheck(res.tree) catch {
      case NonFatal(ex) =>
        val p = c.enclosingPosition
        val msg = s"Error typechecking MappedToBase expansion: ${ex.getMessage}"
        println(s"${p.source.path} : ${p.line} $msg")
        c.error(c.enclosingPosition, msg)
    }
    res
  }
}
