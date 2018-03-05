package com.worthlesscog.gw2

import Utils.{ asString, byName, cmpLeft, dump, dumpAndTally, isNumeric, matchingName, ticked }

class TitlesCommand extends Command {

    val bindings = List("titles")

    def execute(cmd: List[String]): Unit = cmd match {
        case ic :: Nil =>
            if (ic |> isNumeric)
                titles.get(ic.toInt) foreach { _.toMap |> dump(cmpLeft, asString) }
            else
                titles |> matchingName(ic) |> dumpAndTally(byName, ticked(accountTitles))

        case Nil =>
            titles |> dumpAndTally(byName, ticked(accountTitles))

        case _ =>
    }

    val uses = Some(Map("titles [#id | #contains]" -> "list titles"))

}
