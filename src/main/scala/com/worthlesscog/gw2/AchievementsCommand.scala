package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.info

class AchievementsCommand(label: String) extends FlagNameTypeMap[Achievement](label) {

    override def dumpObject(a: Achievement) = {
        val aa = accountAchievements.get(a.id)
        val (steps, count) = stepsDone(a, aa)
        dumpTitle(a.name, count, steps)
        dumpRequirement(a.requirement)
        a.bits foreach { b =>
            val ticks = if (steps == count) (0 to count - 1).toSet else aa.fold(Set.empty[Int]) { _.bits.fold(Set.empty[Int]) { _.toSet } }
            b.zipWithIndex foreach {
                case (a, n) =>
                    val state = if (ticks contains n) TICK else " "
                    val label = a match {
                        case ItemProgress(_, id)    => id map { "Item, " + possiblyMissingName(items, _) }
                        case MinipetProgress(_, id) => id map { "Mini, " + possiblyMissingName(minis, _) }
                        case SkinProgress(_, id)    => id map { "Skin, " + possiblyMissingName(skins, _) }
                        case TextProgress(_, text)  => text
                    }
                    s"    $state  ${label.getOrElse("")}\n" |> info
            }
        }
    }

    def dumpRequirement(s: String) = if (s nonEmpty) s"  ${s}\n" |> info

    def dumpTitle(s: String, count: Int, steps: Int) = s"  $s ($count/$steps)\n" |> info

    def execute(cmd: List[String]) = execute(cmd, achievements, achievementFlags, achievementTypes)

    def possiblyMissingName[V <: Named](m: Map[Int, V], id: Int) = m.get(id).fold(s"#$id Missing")(_.name)

    def stepsDone(a: Achievement, aa: Option[AccountAchievement]) =
        (a.tiers.last.count,
            aa.fold(0) { aa =>
                if (aa done)
                    a.tiers.last.count
                else if (aa.current nonEmpty)
                    aa.current.get
                else
                    aa.bits.fold(0) { _.size }
            })

}
