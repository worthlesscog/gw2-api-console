package com.worthlesscog.gw2

import Utils.{ asString, byName, dumpAndTally, ofDetailType }

class WeaponsCommand extends Command {

    val bindings = List("weapons")

    def execute(cmd: List[String]): Unit = cmd match {
        case t :: Nil =>
            weaponSkins |> ofDetailType(t) |> dumpAndTally(byName, asString)

        case Nil =>
            weaponSkins |> dumpAndTally(byName, asString)

        case _ =>
    }

    val uses = Some(Map("weapons [#type]" -> "list weapon skins"))

}
