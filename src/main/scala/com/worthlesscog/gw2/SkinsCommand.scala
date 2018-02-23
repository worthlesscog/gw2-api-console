package com.worthlesscog.gw2

import Utils.dumpCollections

class SkinsCommand extends FlagNameTypeMap[Skin]("skins") {

    def execute(cmd: List[String]) = cmd match {
        case "collections" :: Nil =>
            skins |> dumpCollections(accountSkins)

        case _ =>
            execute(cmd, skins, skinFlags, skinTypes)
    }

    override def uses = Some(Map("skins [#id | #contains | #flag | #type | collections]" -> "list skins"))

}
