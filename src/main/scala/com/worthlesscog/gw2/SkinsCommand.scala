package com.worthlesscog.gw2

import Utils.{ asString, byName, dumpAndTally, dumpCollections, notFlagged }

class SkinsCommand extends FlagNameTypeMap[Skin]("skins") {

    def execute(cmd: List[String]) = cmd match {
        case "collections" :: Nil =>
            skins |> dumpCollections(accountSkins)

        case "invisible" :: Nil =>
            skins |> notFlagged("ShowInWardrobe") |> dumpAndTally(byName, asString)

        case _ =>
            execute(cmd, skins, skinFlags, skinTypes)
    }

    override def uses = Some(Map("skins [#id | #contains | #flag | #type | collections | invisible]" -> "list skins"))

}
