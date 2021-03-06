package com.worthlesscog.gw2

import Utils._

class PetsCommand extends Command {

    val bindings = List("pets")

    def execute(cmd: List[String]): Unit = cmd match {
        case "cheapest" :: Nil =>
            val updates = minis |> absentFrom(accountMinis) |> priceByItem
            minis = minis ++ updates
            updates |> isPriced |> dumpAndTally(byBuyPrice, priced)

        case "collections" :: Nil =>
            minis |> collectable |> priceByItem |> toCollections |> dumpCollections(tickedAndPriced(accountMinis))

        case "missing" :: Nil =>
            val updates = minis |> absentFrom(accountMinis) |> priceByItem
            minis = minis ++ updates
            updates |> dumpAndTally(byName, priced)

        case ic :: Nil =>
            if (ic |> isNumeric)
                minis.get(ic.toInt) foreach { _.toMap |> dump(cmpLeft, asString) }
            else
                minis |> matchingName(ic) |> dumpAndTally(byName, tickedAndPriced(accountMinis))

        case Nil =>
            minis |> dumpAndTally(byName, tickedAndPriced(accountMinis))

        case _ =>
    }

    def priced(m: Mini): String =
        s"   ${ m.name }${ prices(m) }"

    val uses = Some(Map("pets [#id | #contains | cheapest | collections | missing]" -> "list minipets"))

}
