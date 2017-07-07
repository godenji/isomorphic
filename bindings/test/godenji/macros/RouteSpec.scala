package godenji.macros

import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

import play.api.mvc.PathBindable
import bindable.Route._
import key._

class RouteSpec extends PlaySpec {

  "path bind" must {
    "convert from T#Underlying String value to Right(T)" in {
      val default = UserId.zero
      val underlying =
        implicitly[PathBindable[UserId]].bind("userId", default.value.toString)

      underlying mustEqual Right(default)
    }
  }

  "path unbind on instance *without* custom `toString`" must {
    "convert from T to T#Underlying as String value" in {
      val default = UserId.zero
      val underlying =
        implicitly[PathBindable[UserId]].unbind("userId", default)

      underlying mustEqual default.toString
    }
  }

  "path unbind on instance *with* custom `toString`" must {
    "convert from T to T#Underlying as `toString` applied value" in {
      val default = Enum.zero
      val underlying =
        implicitly[PathBindable[Enum]].unbind("enum", default)

      underlying mustEqual default.toString
    }
  }
}