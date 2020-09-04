Isomorphic
===============

Provides a macro generated implicit instance that converts to/from a value class and 
its underlying primitive type. Can be used standalone, or with the Play Framework for 
automatic handling of routes and forms that depend on value class based 
routes and form fields.

Installation
----------------------

The library comes in two flavors: the standalone version that provides the macro 
generated implicit materializer; the Play version which adds support for 
automatic conversion of HTTP routes and form bindings. For users of the Slick database 
library there's a Slick-dependent version available as well.

```
// standalone
libraryDependencies += "io.github.godenji" % "isomorphic" % "0.1.7"

// with Play Framework route and form binding support
libraryDependencies += "io.github.godenji" % "isomorphic-play" % "0.1.7"

// Slick standalone
libraryDependencies += "io.github.godenji" % "isomorphic-slick" % "0.1.7"

// Slick with Play Framework route and form binding support
libraryDependencies += "io.github.godenji" % "isomorphic-play-slick" % "0.1.7"
```

Example HTTP route transformation (Play Framework)
---------------------
Define your wrapped type:
```
import godenji.iso.MappedTo

case class PlayerId(value: Long) extends AnyVal with MappedTo[Long]
```
along with route converter added to your build.sbt project settings:
```
routesImport += "godenji.iso.bindable.Route._"
```

then given an http route in your conf file like:
`/player/:id controllers.Player.show(id: PlayerId)`

and an http request URI like `/player/42`, your controller's show method  will have a 
`PlayerId(42)` available.

Example form binding (Play Framework)
--------------------
```
case class Player(
  id: Option[PlayerId],
  firstName: String,
  lastName: String,
  email: String
)

object PlayerForm {
  import play.api.data.{Form, Forms}, Forms._, play.api.data.format.Formats._
  import godenji.iso.bindable.Form._

  val mapper = mapping(
    "id" -> optional(of[PlayerId]),
    "firstName" -> nonEmptyText,
    "lastName" -> nonEmptyText,
    "email" -> nonEmptyText
  )(Player.apply)(Player.unapply)
  val form = Form(mapper)
}
```

For additional uses cases take a look at the tests under `bindings` and `bindings-slick` directories.
