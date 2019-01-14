package com.worthlesscog.gw2

import Utils.{asString, byName, dumpAndTally, ofDetailType, ofWeight}

class ArmorCommand extends Command {

    val bindings = List("armor")

    def execute(cmd: List[String]): Unit = cmd match {
        case w :: t :: Nil =>
            armorSkins |> ofWeight(w) |> ofDetailType(t) |> dumpAndTally(byName, asString)

        case wt :: Nil =>
            if (armorWeights contains wt)
                armorSkins |> ofWeight(wt) |> dumpAndTally(byName, asString)
            else if (armorTypes contains wt)
                armorSkins |> ofDetailType(wt) |> dumpAndTally(byName, asString)

        case Nil =>
            armorSkins |> dumpAndTally(byName, asString)

        case _ =>
    }

    val uses = Some(Map("armor [#weight] [#type]" -> "list armor skins"))

}
