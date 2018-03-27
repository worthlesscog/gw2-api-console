package com.worthlesscog.gw2

import Utils.{ asString, byName, dumpAndTally, dumpCollections, notFlagged, repriceSkin, tickedAndPriced, toCollections }

class SkinsCommand extends FlagNameTypeMap[Skin]("skins") {

    def execute(cmd: List[String]) = cmd match {
        case "collections" :: Nil =>
            skins |> repriceSkin |> toCollections |> dumpCollections(tickedAndPriced(accountSkins))

        case "invisible" :: Nil =>
            skins |> notFlagged("ShowInWardrobe") |> dumpAndTally(byName, asString)

        case _ =>
            execute(cmd, skins, skinFlags, skinTypes)
    }

    override def uses = Some(Map(s"skins [$ID_CONT_FLAG_TYPE | collections | invisible]" -> "list skins"))

}
