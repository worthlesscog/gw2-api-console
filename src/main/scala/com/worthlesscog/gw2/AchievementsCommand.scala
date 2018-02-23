package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.info

class AchievementsCommand extends FlagNameTypeMap[Achievement]("achievements") {

    override def dumpObject(a: Achievement) = {
        val aa = accountAchievements.get(a.id)
        val last = a.tiers.last.count
        val done = aa.fold(0) { a =>
            if (a done)
                last
            else if (a.current nonEmpty)
                a.current.get
            else
                a.bits.fold(0) { _.size }
        }
        dumpTitle(a.name, done, last)
        dumpRequirement(a.requirement)
        a.bits foreach { b =>
            val ticks = if (done == last) (0 to last - 1).toSet else aa.fold(Set.empty[Int]) { _.bits.fold(Set.empty[Int]) { _.toSet } }
            b.zipWithIndex foreach {
                case (a, n) =>
                    val state = if (ticks contains n) TICK else " "
                    val label = a match {
                        case ItemProgress(_, id)    => id map { "Item, " + items(_).name }
                        case MinipetProgress(_, id) => id map { "Mini, " + minis(_).name }
                        case SkinProgress(_, id)    => id map { "Skin, " + skins(_).name }
                        case TextProgress(_, text)  => text
                    }
                    s"    $state  ${label.getOrElse("")}\n" |> info
            }
        }
    }

    def dumpRequirement(s: String) = if (s nonEmpty) s"  ${s}\n" |> info

    def dumpTitle(s: String, n: Int, m: Int) = s"  $s ($n/$m)\n" |> info

    def execute(cmd: List[String]) = execute(cmd, achievements, achievementFlags, achievementTypes)

}
