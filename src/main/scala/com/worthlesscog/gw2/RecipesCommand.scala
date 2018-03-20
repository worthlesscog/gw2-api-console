package com.worthlesscog.gw2

import Utils.{ absentFrom, asString, cmpLeft, cmpRight, dump, dumpAndTally, isNumeric, ofType, presentIn, repriceRecipe, toStringPrice }

class RecipesCommand extends Command {

    val bindings = List("recipes")

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
                recipes |> forDiscipline(cdt) |> repriceRecipe |> dumpAbsent(accountRecipes)
            else if (recipeTypes contains cdt)
                recipes |> ofType(cdt) |> repriceRecipe |> dumpAbsent(accountRecipes)
            else
                recipes |> matching(cdt) |> repriceRecipe |> dumpAbsent(accountRecipes)

        case "unlocked" :: cdt :: Nil =>
            if (disciplines contains cdt)
                recipes |> forDiscipline(cdt) |> dumpPresent(accountRecipes)
            else if (recipeTypes contains cdt)
                recipes |> ofType(cdt) |> dumpPresent(accountRecipes)
            else
                recipes |> matching(cdt) |> dumpPresent(accountRecipes)

        case "locked" :: Nil =>
            recipes |> repriceRecipe |> dumpAbsent(accountRecipes)

        case "unlocked" :: Nil =>
            recipes |> dumpPresent(accountRecipes)

        case cdt :: Nil =>
            if (cdt |> isNumeric)
                recipes.get(cdt.toInt) foreach {
                    _.toMap |> dump(cmpLeft, asString)
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

    def toItems(m: Map[_, Recipe]) =
        m map {
            case (k, r) =>
                val o = r.output_item_id
                k -> items.get(o).fold(s"Item #$o Missing")(i => s"${i.name}${r.sell.fold("") { ", " + toStringPrice(_) }}")
        }

    def unlockable(m: Map[_, Recipe]) =
        m filter { case (_, r) => r.flags contains "LearnedFromItem" }

    val uses = Some(Map("recipes [#id | [locked | unlocked] [#contains | #discipline | #type]]" -> "list recipes"))

}
