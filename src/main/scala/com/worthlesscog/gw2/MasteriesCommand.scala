package com.worthlesscog.gw2

import Utils.{ asString, byName, dumpAndTally }

class MasteriesCommand extends Command {

    val bindings = List("masteries")

    def execute(cmd: List[String]): Unit = cmd match {
        case Nil =>
            masteries |> dumpAndTally(byName, asString)

        case _ =>
    }

    val uses = Some(Map("masteries" -> "list masteries"))

}
