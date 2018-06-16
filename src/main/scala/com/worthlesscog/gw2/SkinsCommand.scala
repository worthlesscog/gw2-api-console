package com.worthlesscog.gw2

import Utils.{ asString, byName, collectable, dumpAndTally, dumpCollections, flagged, notFlagged, priceSkins, prices, tickedAndPriced, toCollections, toStringPrice }

class SkinsCommand extends FlagNameTypeMap[Skin]("skins") {

    def byBuyPrice(a: (_, Skin), b: (_, Skin)) = Ordering[Option[Int]].lt(a._2.buy, b._2.buy)
    def bySellPrice(a: (_, Skin), b: (_, Skin)) = Ordering[Option[Int]].lt(a._2.sell, b._2.sell)

    def execute(cmd: List[String]) = cmd match {
        case "cheapest" :: Nil =>
            val missing = skins |> flagged("ShowInWardrobe") filter { case (_, s) => !(accountSkins contains s.id) }
            val withPrices = missing |> priceSkins
            skins = skins ++ withPrices
            val actuallyPriced = withPrices filter { case (_, s) => s.sell.nonEmpty && s.sell.get > 0 }
            actuallyPriced |> dumpAndTally(byBuyPrice, outbids(accountBuys), 50)

        case "collections" :: Nil =>
            val priced = skins |> collectable |> priceSkins
            skins = skins ++ priced
            priced |> toCollections |> dumpCollections(tickedAndPriced(accountSkins), Some(totals))

        case "invisible" :: Nil =>
            skins |> notFlagged(Set("ShowInWardrobe")) |> dumpAndTally(byName, asString)

        case _ =>
            execute(cmd, skins, skinFlags, skinTypes)
    }

    def outbids(tx: Map[Int, AccountBuy])(s: Skin): String = {
        val is = skinToItems.get(s.id).fold(Map.empty[Int, Int]) { _ flatMap { items.get(_) flatMap { i => i.buy map { i.id -> _ } } } toMap }
        val ts = tx map { case (_, t) => t.item_id -> t.price }
        val state = if (accountSkins contains s.id)
            "*"
        else if (is.keySet intersect ts.keySet isEmpty)
            " "
        else if (is.exists { case (i, b) => ts.contains(i) && b > ts(i) })
            ">"
        else
            "="
        s"$state  ${s.name}${prices(s)}"
    }

    def totals(m: Map[Int, Skin]) = {
        val (b, s) = m.filter { case (k, _) => !accountSkins.contains(k) }.foldLeft((0, 0)) { case ((bt, st), (_, s)) => (s.buy.fold(bt)(bt +), s.sell.fold(st)(st +)) }
        val bp = if (b > 0) Some("B " + toStringPrice(b)) else None
        val sp = if (s > 0) Some("S " + toStringPrice(s)) else None
        val t = Seq(bp, sp).flatten.mkString("   ", ", ", "")
        if (b > 0 || s > 0) Some(t) else None
    }

    override def uses = Some(Map(s"skins [$ID_CONT_FLAG_TYPE | cheapest | collections | invisible]" -> "list skins"))

}
