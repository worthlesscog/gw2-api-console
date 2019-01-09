package com.worthlesscog.gw2

import Utils.{ asString, byName, cmpRight, dumpAndTally, toItems }

class NewCommand extends Command {

    val bindings = List("new")

    def execute(cmd: List[String]): Unit = cmd match {
        case _ =>
            newAchievementCategories |> dumpAndTally(byName, asString)
            newAchievementGroups |> dumpAndTally(byName, asString)
            newAchievements |> dumpAndTally(byName, asString)
            newColors |> dumpAndTally(byName, asString)
            newItems |> dumpAndTally(byName, asString)
            newItemStats |> dumpAndTally(byName, asString)
            newMasteries |> dumpAndTally(byName, asString)
            newMinis |> dumpAndTally(byName, asString)
            newRaces |> dumpAndTally(byName, asString)
            newRecipes |> toItems |> dumpAndTally(cmpRight, asString)
            newSkins |> dumpAndTally(byName, asString)
            newTitles |> dumpAndTally(byName, asString)
    }

    val uses = Some(Map("new" -> "list new items"))

}
