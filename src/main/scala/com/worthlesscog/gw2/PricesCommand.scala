package com.worthlesscog.gw2

import Utils.{byBuyPrice, dumpAndTally, isPriced, matchingName, notFlagged, priceItems, prices}

class PricesCommand extends Command {

    val bindings = List("prices")

    val bound = Set("AccoundBound", "SoulbindOnAcquire")

    def execute(cmd: List[String]): Unit = cmd match {
        case c :: Nil =>
            val updates = items |> matchingName(c) |> notFlagged(bound) |> priceItems
            // dumpAndTally(byName, tickedAndPriced(accountMinis))
            items = items ++ updates
            updates |> isPriced |> dumpAndTally(byBuyPrice, priced)

        case _ =>
    }

    def priced(i: Item): String =
        s"   ${ i.name }${ prices(i) }"

    val uses = Some(Map("prices #contains" -> "price items"))

}
