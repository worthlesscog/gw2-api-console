package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.{ asString, byName, categorized, cmpLeft, collectable, dump, dumpAndTally, dumpCollections, isNumeric, matchingName, priceByItem, ticked, tickedAndPriced, toCollections, toStringPrice }

class DyesCommand extends Command {

    val bindings = List("dyes")
    val harvestCategories = Set("Red", "Brown", "Blue", "Green", "Purple", "Gray", "Yellow", "Orange")

    def byBuyPrice(a: (_, Color), b: (_, Color)) = Ordering[Option[Int]].lt(a._2.buy, b._2.buy)

    def execute(cmd: List[String]): Unit = cmd match {
        case "cheapest" :: Nil =>
            val missing = colors filter { case (_, d) => !(accountDyes contains d.id) }
            val withPrices = missing |> priceByItem
            colors = colors ++ withPrices
            val actuallyPriced = withPrices filter { case (_, s) => s.sell.nonEmpty && s.sell.get > 0 }
            actuallyPriced |> dumpAndTally(byBuyPrice, withCollectionAndBuyPrice)

        case "collections" :: Nil =>
            colors |> collectable |> priceByItem |> toCollections |> dumpCollections(tickedAndPriced(accountDyes), Some(totals))

        case "harvest" :: Nil =>
            colors |> harvestable |> priceByItem |> dumpAndTally(byName, withCategoriesAndSellPrice)

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
            case (_, c) => !(accountDyes contains c.id) && (c.collection isEmpty) && !(c.categories isEmpty)
        }

    def totals(m: Map[Int, Color]) = {
        val t = m.filter { case (k, _) => !accountDyes.contains(k) }.foldLeft(0) { case (t, (_, s)) => s.buy.fold(t)(t +) }
        if (t > 0) Some(s"   ${toStringPrice(t)} to complete") else None
    }

    def withCategoriesAndSellPrice(c: Color) = s"${c.name}, ${c.sell.fold("-") { toStringPrice }} (${harvestCategories.intersect(c.categories).mkString(",")})"

    def withCollectionAndBuyPrice(c: Color) = s"${c.name}, ${c.buy.fold("-") { toStringPrice }}${c.collection.fold("")(s => " (" + s + ")")}"

    val uses = Some(Map("dyes [#id | #category | #contains | cheapest | collections | harvest]" -> "list dyes"))

}
