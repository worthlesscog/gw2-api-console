package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.{ byName, dumpAndTally }

class CollectionsCommand extends AchievementsCommand("collections") {

    def completed(a: Achievement): String = {
        val (steps, count) = stepsDone(achievements(a.id), accountAchievements.get(a.id))
        val (state, progress) = if (count == steps) (TICK, "") else (" ", s" ($count/$steps)")
        s"$state  ${a.name}$progress"
    }

    override def execute(cmd: List[String]): Unit = cmd match {
        case Nil =>
            collections |> dumpAndTally(byName, completed)

        case _ =>
            execute(cmd, collections, achievementFlags, achievementTypes)
    }

    override def uses = Some(Map(s"collections [$ID_CONT_FLAG_TYPE]" -> s"list achievement collections"))

}
