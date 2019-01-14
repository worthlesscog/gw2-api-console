package com.worthlesscog.gw2

import Utils.{asString, byName, dumpAndTally}

class AchievementCategoriesCommand extends Command {

    val bindings = List("categories")

    def execute(cmd: List[String]): Unit = {
        cmd match {
            case Nil =>
                achievementCategories |> dumpAndTally(byName, asString)

            case _ =>
        }
    }

    val uses = Some(Map("categories" -> "list achievement categories"))

}
