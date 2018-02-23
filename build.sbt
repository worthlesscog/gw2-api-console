name := "Guild Wars 2 API Console"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-feature", "-language:postfixOps", "-language:reflectiveCalls")

// libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.3"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.1"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.3"

mainClass in assembly := Some("com.worthlesscog.gw2.ApiConsole")
test in assembly := {}
