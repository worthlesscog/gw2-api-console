package com.worthlesscog.gw2

import Utils._

class RecipesCommand extends Command {

    val bindings = List("recipes")

    trait Component {
        def count: Int
        def name: String
    }
    case class CompositeComponent(name: String, count: Int, components: List[(Int, Component)]) extends Component
    case class SimpleComponent(name: String, count: Int) extends Component

    // XXX - ignores guild upgrades

    def craft(r: Recipe): Component = {

        def recipeFor(id: Int) = recipes.values find { _.output_item_id == id }

        CompositeComponent(nameOrId(items, r.output_item_id), r.output_item_count, r.ingredients map {
            case ItemCount(id, count) =>
                (count, recipeFor(id).fold { SimpleComponent(nameOrId(items, id), count): Component } { craft })
        })

    }

    def dumpCraft(c: Component): Unit = {
        def tree(count: Int, multiplier: Int, c: Component, inset: String): List[(String, Int)] = c match {
            case SimpleComponent(name, outputCount) =>
                s"$inset$count x $name\n" |> info
                List((name, count * multiplier))

            case CompositeComponent(name, outputCount, l) =>
                s"$inset$count x $name\n" |> info
                l flatMap {
                    case (n, c) => tree(n, (count * multiplier) / outputCount, c, inset + "  ")
                }
        }

        val l = tree(c.count, 1, c, "  ")
        val tally = l.groupBy(_._1).map { case (n, l) => n -> l.foldLeft(0) { case (t, (_, n)) => t + n } }
        newLine
        tally |> dump(cmpLeft, asString)
    }

    def dumpAbsent[K](a: Set[K])(m: Map[K, Recipe]) =
        m |> absentFrom(a) |> dumpUnlockable

    def dumpItems(m: Map[_, Recipe]) =
        m |> toItems |> dumpAndTally(cmpRight, asString)

    def dumpPresent[K](a: Set[K])(m: Map[K, Recipe]) =
        m |> presentIn(a) |> dumpUnlockable

    def dumpUnlockable(m: Map[_, Recipe]) =
        // m |> dumpItems
        m |> unlockable |> dumpItems

    def execute(cmd: List[String]): Unit = cmd match {
        case "locked" :: cdt :: Nil =>
            if (disciplines contains cdt)
                recipes |> forDiscipline(cdt) |> priceRecipes |> dumpAbsent(accountRecipes)
            else if (recipeTypes contains cdt)
                recipes |> ofType(cdt) |> priceRecipes |> dumpAbsent(accountRecipes)
            else
                recipes |> matching(cdt) |> priceRecipes |> dumpAbsent(accountRecipes)

        case "unlocked" :: cdt :: Nil =>
            if (disciplines contains cdt)
                recipes |> forDiscipline(cdt) |> dumpPresent(accountRecipes)
            else if (recipeTypes contains cdt)
                recipes |> ofType(cdt) |> dumpPresent(accountRecipes)
            else
                recipes |> matching(cdt) |> dumpPresent(accountRecipes)

        case "locked" :: Nil =>
            recipes = priceRecipes(recipes)
            recipes |> dumpAbsent(accountRecipes)

        case "unlocked" :: Nil =>
            recipes |> dumpPresent(accountRecipes)

        case cdt :: Nil =>
            if (cdt |> isNumeric)
                recipes.get(cdt.toInt) foreach { r =>
                    r.toMap |> dump(cmpLeft, asString)
                    newLine
                    craft(r) |> dumpCraft
                }
            else if (disciplines contains cdt)
                recipes |> forDiscipline(cdt) |> dumpUnlockable
            else if (recipeTypes contains cdt)
                recipes |> ofType(cdt) |> dumpUnlockable
            else
                recipes |> matching(cdt) |> dumpUnlockable

        case Nil =>
            recipes |> dumpItems

        case _ =>
    }

    def forDiscipline[K](d: String)(m: Map[K, Recipe]) =
        m filter { case (_, r) => r.disciplines contains d }

    def matching[K](c: String)(m: Map[K, Recipe]) =
        m filter { case (_, r) => items.get(r.output_item_id).fold(false) { _.name.contains(c) } }

    def unlockable(m: Map[_, Recipe]) =
        m filter { case (_, r) => r.flags contains "LearnedFromItem" }

    val uses = Some(Map("recipes [#id | [locked | unlocked] [#contains | #discipline | #type]]" -> "list recipes"))

}
