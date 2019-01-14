package com.worthlesscog.gw2

import Utils.{asString, byName, cmpRight, dump, dumpAndTally, info, isNumeric}

class ItemStatsCommand extends Command {

    val bindings = List("stats")

    def execute(cmd: List[String]): Unit = cmd match {
        case i :: Nil =>
            if (i |> isNumeric)
                itemStats.get(i.toInt) foreach {
                    _.toMap |> dump(cmpRight, asString)
                }
            else
                "??\n" |> info

        case Nil =>
            itemStats |> dumpAndTally(byName, asString)

        case _ =>
    }

    val uses = Some(Map("stats [#id]" -> "list item stat sets"))

}
