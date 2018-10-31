package com.worthlesscog.gw2

import Utils.{ dumpAndTally, info, nameOrId, prices }

class AchievementsCommand(label: String) extends FlagNameTypeMap[Achievement](label) {

    def completed(a: Achievement): String = {
        val (steps, count) = stepsDone(achievements(a.id), accountAchievements.get(a.id))
        val progress = if (count == steps) "" else s" ($count/$steps)"
        val hidden = if (a.isVisible) "" else " *"
        s"${a.name}$progress$hidden"
    }

    def describeItem(id: Int) = "Item, " + nameOrId(items, id) + maybeRarity(items, id) + maybePrices(items, id)

    def describeMini(id: Int) = "Mini, " + nameOrId(minis, id) + maybePrices(minis, id)

    def describeSkin(id: Int) = "Skin, " + nameOrId(skins, id) + maybePrices(skins, id)

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
                        case ItemProgress(_, id)    => id map describeItem
                        case MinipetProgress(_, id) => id map describeMini
                        case SkinProgress(_, id)    => id map describeSkin
                        case TextProgress(_, text)  => text
                    }
                    s"    $state  ${label.getOrElse("")}\n" |> info
            }
        }
    }

    def dumpRequirement(s: String) = if (s nonEmpty) s"  ${s}\n" |> info

    def dumpTitle(s: String, count: Int, steps: Int) = s"  $s ($count/$steps)\n" |> info

    def dumpTree(filter: (Achievement) => Boolean) = {
        achievementGroups.values.filter(!_.categories.isEmpty).toList.sortBy(_.order) foreach { g =>
            s"  ${g.name} (${g.id})\n" |> info
            g.categories.map(achievementCategories).filter(!_.achievements.isEmpty).toList.sortBy(_.order) foreach { c =>
                val l = c.achievements.map(achievements).toList.sortBy(_.name).filter(filter)
                if (l nonEmpty) {
                    s"    ${printable(c.name)} (${c.id})\n" |> info
                    l foreach { a =>
                        s"      ${completed(a)}\n" |> info
                    }
                }
            }
        }
    }

    override def execute(cmd: List[String]): Unit = cmd match {
        case "hidden" :: Nil =>
            dumpTree((a) => !(a.isVisible))

        case "nearly" :: Nil =>
            achievements |> started |> incomplete |> dumpAndTally(nearly, completed, 50)

        case "tree" :: Nil =>
            dumpTree((a) => true)

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

    def maybePrices[T <: Priced[T]](m: Map[Int, T], id: Int) = m.get(id).fold("") { prices(_) }

    def maybeRarity(m: Map[Int, Item], id: Int) = m.get(id).fold("")(i => ", " + i.rarity)

    def nearly(a: (_, Achievement), b: (_, Achievement)) = {
        val (c1, s1) = stepsDone(a._2, accountAchievements.get(a._2.id))
        val (c2, s2) = stepsDone(b._2, accountAchievements.get(b._2.id))
        (c1.toFloat / s1 < c2.toFloat / s2)
    }

    def printable(s: String) = s.filter(!_.isControl)

    def started(a: Achievement) = {
        val (_, count) = stepsDone(achievements(a.id), accountAchievements.get(a.id))
        count > 0
    }

    def started(m: Map[_, Achievement]): Map[_, Achievement] =
        m filter { case (_, a) => started(a) }

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

    override def uses = Some(Map(s"achievements [$ID_CONT_FLAG_TYPE | hidden | nearly | tree]" -> s"list achievements"))

}
