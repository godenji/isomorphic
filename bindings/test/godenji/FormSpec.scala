package godenji.iso

import org.scalatestplus.play._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import play.api.data.FormBinding.Implicits._

import model.{ User, UserForm }
import bindable.Form._
import key._

class FormSpec extends PlaySpec {

  "form bind from http request body" must {
    "convert a T#Underlying String value to Right(T)" in {
      implicit val req = FakeRequest().withFormUrlEncodedBody(
        "id" -> "42",
        "firstName" -> "John",
        "lastName" -> "Coltrane",
        "email" -> "alove@supreme.org"
      )
      val res = UserForm.form.bindFromRequest().fold(
        Left(_), Right(_)
      )
      res mustEqual Right(
        User(UserId(42), "John", "Coltrane", "alove@supreme.org")
      )
    }
  }
}
