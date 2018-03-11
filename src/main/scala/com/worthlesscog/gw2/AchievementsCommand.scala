package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.{ dumpAndTally, info }

class AchievementsCommand(label: String) extends FlagNameTypeMap[Achievement](label) {

    def completed(a: Achievement): String = {
        val (steps, count) = stepsDone(achievements(a.id), accountAchievements.get(a.id))
        val (state, progress) = if (count == steps) (TICK, "") else (" ", s" ($count/$steps)")
        s"$state  ${a.name}$progress"
    }

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

    override def execute(cmd: List[String]): Unit = cmd match {
        case "nearly" :: Nil =>
            achievements |> started |> incomplete |> dumpAndTally(nearly, completed)

        case _ =>
            execute(cmd, achievements, achievementFlags, achievementTypes)
    }

    def incomplete(a: Achievement) = {
        val (steps, count) = stepsDone(achievements(a.id), accountAchievements.get(a.id))
        steps != count
    }

    def incomplete(m: Map[_, Achievement]): Map[_, Achievement] =
        m filter {
            case (_, a) => incomplete(a)
        }

    def nearly(a: (_, Achievement), b: (_, Achievement)) = {
        val (c1, s1) = stepsDone(a._2, accountAchievements.get(a._2.id))
        val (c2, s2) = stepsDone(b._2, accountAchievements.get(b._2.id))
        (c1.toFloat / s1 < c2.toFloat / s2)
    }

    def possiblyMissingName[V <: Named](m: Map[Int, V], id: Int) = m.get(id).fold(s"#$id Missing")(_.name)

    def started(a: Achievement) = {
        val (steps, count) = stepsDone(achievements(a.id), accountAchievements.get(a.id))
        count > 0
    }

    def started(m: Map[_, Achievement]): Map[_, Achievement] =
        m filter {
            case (_, a) => started(a)
        }

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

    override def uses = Some(Map(s"achievements [$ID_CONT_FLAG_TYPE | nearly]" -> s"list achievements"))

}
