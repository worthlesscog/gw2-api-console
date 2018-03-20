package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.{ asString, byName, categorized, cmpLeft, collectable, dump, dumpAndTally, dumpCollections, isNumeric, matchingName, repriceByItem, ticked, tickedAndPriced, toCollections, toStringPrice }

class DyesCommand extends Command {

    val bindings = List("dyes")
    val harvestCategories = Set("Red", "Brown", "Blue", "Green", "Purple", "Gray", "Yellow", "Orange")

    def execute(cmd: List[String]): Unit = cmd match {
        case "collections" :: Nil =>
            colors |> collectable |> repriceByItem |> toCollections |> dumpCollections(tickedAndPriced(accountDyes))

        case "harvest" :: Nil =>
            colors |> harvestable |> repriceByItem |> dumpAndTally(byName, withCategoriesAndPrices)

        case icc :: Nil =>
            if (icc |> isNumeric) {
                colors.get(icc.toInt) foreach { _.toMap |> dump(cmpLeft, asString) }
            } else if (colorCategories contains icc)
                colors |> categorized(icc) |> dumpAndTally(byName, ticked(accountDyes))
            else
                colors |> matchingName(icc) |> dumpAndTally(byName, ticked(accountDyes))

        case Nil =>
            colors |> dumpAndTally(byName, ticked(accountDyes))

        case _ =>
    }

    def harvestable[K](m: Map[K, Color]) =
        m filter {
            case (_, c) =>
                !(accountDyes contains c.id) && (c.collection isEmpty) && !(c.categories isEmpty)
        }

    def withCategoriesAndPrices(c: Color) = s"${c.name}, ${c.sell.fold("-") { toStringPrice }} (${harvestCategories.intersect(c.categories).mkString(",")})"

    val uses = Some(Map("dyes [#id | #category | #contains | collections | harvest]" -> "list dyes"))

}
