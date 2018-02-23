package com.worthlesscog.gw2

import Utils.{ asString, byName, dumpAndTally, notFlagged }

class InvisibleCommand extends Command {

    val bindings = List("invisible")

    def execute(cmd: List[String]): Unit = cmd match {
        case Nil =>
            skins |> notFlagged("ShowInWardrobe") |> dumpAndTally(byName, asString)

        case _ =>
    }

    val uses = Some(Map("invisible" -> "list invisible skins"))

}
