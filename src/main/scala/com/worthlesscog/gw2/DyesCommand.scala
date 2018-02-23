package com.worthlesscog.gw2

import scala.language.postfixOps

import GuildWarsData.dyeSets
import Utils.{ asString, byName, categorized, dump, dumpAndTally, info, isNumeric, matchingName, cmpLeft, ticked, toStringPrice }

class DyesCommand extends Command {

    val bindings = List("dyes")
    val harvestCategories = Set("Red", "Brown", "Blue", "Green", "Purple", "Gray", "Yellow", "Orange")

    def execute(cmd: List[String]): Unit = cmd match {
        case "collections" :: Nil =>
            dyeSets foreach {
                case (n, s) =>
                    s"$n\n" |> info
                    colors.filter { case (_, c) => s contains c.name } |> dump(byName, ticked(accountDyes))
            }

        case "harvest" :: Nil => {
            val missing = colors filter harvestable
            val byItem = missing flatMap { case (_, c) => c.item.map { _ -> c } }
            val prices = byItem.keys |> Commerce.prices
            val withPrices = prices.foldLeft(missing) { (m, ip) =>
                ip match {
                    case (i, p) =>
                        val nc = byItem(i)
                        m + (nc.id -> (nc |> updatePrice(p)))
                }
            }
            withPrices |> dumpAndTally(byName, withCategoriesAndPrices)
        }

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

    def harvestable(c: (_, Color)) = c match {
        case (_, c) =>
            !(accountDyes contains c.id) && (c.collection isEmpty) && !(c.categories isEmpty)
    }

    def updatePrice(p: Price)(c: Color) =
        c.copy(buy = Some(p.buys.fold(0)(_.unit_price)), sell = Some(p.sells.fold(0)(_.unit_price)))

    def withCategoriesAndPrices(c: Color) = s"${c.name}, ${c.sell.fold("-") { toStringPrice }} (${harvestCategories.intersect(c.categories).mkString(",")})"

    val uses = Some(Map("dyes [#id | #category | #contains | collections | harvest]" -> "list dyes"))

}
