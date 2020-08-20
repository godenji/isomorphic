package godenji.iso
package model

import key._

case class User(
  id: UserId,
  firstName: String,
  lastName: String,
  email: String
)

object UserForm {
  import play.api.data.{Form, Forms}, Forms._, play.api.data.format.Formats._
  import bindable.Form._

  val mapper = mapping(
    "id" -> default(of[UserId], UserId.zero),
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText,
    "email" -> nonEmptyText
  )(User.apply)(User.unapply)
  val form = Form(mapper)
}
