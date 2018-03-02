package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.{ asString, byName, cmpLeft, collectable, dump, dumpAndTally, dumpCollections, isNumeric, matchingName, reprice, ticked, tickedAndPriced, toCollections }

class PetsCommand extends Command {

    val bindings = List("pets")

    val bound = Set("AccoundBound", "SoulbindOnAcquire")

    def execute(cmd: List[String]): Unit = cmd match {
        case "collections" :: Nil =>
            minis |> collectable |> reprice |> toCollections |> dumpCollections(tickedAndPriced(accountMinis))

        case "trade" :: Nil =>
        //            val missing = minis filterNot { case (_, m) => accountMinis contains m.id }
        //            val byItem = missing map { case (_, m) => m.item_id -> m }

        case ic :: Nil =>
            if (ic |> isNumeric)
                minis.get(ic.toInt) foreach { _.toMap |> dump(cmpLeft, asString) }
            else
                minis |> matchingName(ic) |> dumpAndTally(byName, ticked(accountMinis))

        case Nil =>
            minis |> dumpAndTally(byName, ticked(accountMinis))

        case _ =>
    }

    val uses = Some(Map("pets [#id | #contains | collections | trade]" -> "list minipets"))

}
