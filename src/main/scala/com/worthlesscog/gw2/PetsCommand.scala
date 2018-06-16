package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.{ asString, byName, cmpLeft, collectable, dump, dumpAndTally, dumpCollections, isNumeric, matchingName, priceByItem, prices, ticked, tickedAndPriced, toCollections }

class PetsCommand extends Command {

    val bindings = List("pets")

    val bound = Set("AccoundBound", "SoulbindOnAcquire")

    def execute(cmd: List[String]): Unit = cmd match {
        case "collections" :: Nil =>
            minis |> collectable |> priceByItem |> toCollections |> dumpCollections(tickedAndPriced(accountMinis))

        case "missing" :: Nil =>
            val missing = minis filterNot { case (_, m) => accountMinis contains m.id }
            val updates = missing |> priceByItem
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
        s"   ${m.name}${prices(m)}"

    val uses = Some(Map("pets [#id | #contains | collections | missing]" -> "list minipets"))

}
