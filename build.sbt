name := "Guild Wars 2 API Console"

scalaVersion := "2.12.7"

scalacOptions ++= Seq(
	"-feature", 
	"-language:postfixOps", 
	"-language:reflectiveCalls")

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.3"

mainClass in assembly := Some("com.worthlesscog.gw2.ApiConsole")
test in assembly := {}
