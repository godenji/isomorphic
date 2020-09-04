package godenji.iso

import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

import play.api.mvc.{PathBindable, QueryStringBindable}
import bindable.Route._
import key._

class RouteSpec extends PlaySpec {
  val default = UserId.zero

  "path bind" must {
    "convert from T#Underlying String value to Right(T)" in {
      val underlying =
        implicitly[PathBindable[UserId]].bind("userId", default.value.toString)

      underlying mustEqual Right(default)
    }
  }

  "path unbind " must {
    "convert from T to T#Underlying as String value" in {
      val underlying =
        implicitly[PathBindable[UserId]].unbind("userId", default)

      underlying mustEqual default.toString
    }
  }

  "querystring bind" must {
    "convert from T#Underlying String value to Some(Right(T))" in {
      val underlying =
        implicitly[QueryStringBindable[UserId]].bind("userId", Map("userId" -> Seq("0")))

      underlying mustEqual Some(Right(default))
    }
  }

  "querystring unbind" must {
    "convert from T to String key=value fragment" in {
      val underlying =
        implicitly[QueryStringBindable[UserId]].unbind("userId", default)

      underlying mustEqual s"userId=${default.value}"
    }
  }
}
