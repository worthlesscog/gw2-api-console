package com.worthlesscog.gw2

import Utils._

class MountsCommand extends Command {

    val bindings = List("mounts")

    def execute(cmd: List[String]): Unit = cmd match {
        case "collections" :: Nil =>
            mounts |> collectable |> toCollections |> dumpCollections(ticked(accountMounts))

        case ict :: Nil =>
            if (ict |> isNumeric)
                mounts.get(ict.toInt) foreach { _.toMap |> dump(cmpLeft, asString) }
            else if (mountTypes contains ict)
                mounts |> ofType(ict) |> dumpAndTally(byName, ticked(accountMounts))
            else
                mounts |> matchingName(ict) |> dumpAndTally(byName, ticked(accountMounts))

        case Nil =>
            mounts |> dumpAndTally(byName, ticked(accountMounts))

        case _ =>
    }

    val uses = Some(Map("mounts [#id | #contains | #type | collections]" -> "list mounts"))

}
