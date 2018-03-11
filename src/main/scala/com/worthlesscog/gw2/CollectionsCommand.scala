package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.{ byName, dumpAndTally }

class CollectionsCommand extends AchievementsCommand("collections") {

    override def execute(cmd: List[String]): Unit = cmd match {
        case "nearly" :: Nil =>
            collections |> started |> incomplete |> dumpAndTally(nearly, completed)

        case Nil =>
            collections |> dumpAndTally(byName, completed)

        case _ =>
            execute(cmd, collections, achievementFlags, achievementTypes)
    }

    override def uses = Some(Map(s"collections [$ID_CONT_FLAG_TYPE | nearly]" -> s"list achievement collections"))

}
