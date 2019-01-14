package com.worthlesscog.gw2

import Utils.{asString, byName, dumpAndTally, ofDetailType, priceSkins}

class WeaponsCommand extends SkinsCommand {

    override val bindings = List("weapons")

    override def execute(cmd: List[String]): Unit = cmd match {
        case t :: Nil =>
            weaponSkins |> ofDetailType(t) |> priceSkins |> dumpAndTally(byName, outbids(accountBuys))

        case Nil =>
            weaponSkins |> dumpAndTally(byName, asString)

        case _ =>
    }

    override val uses = Some(Map("weapons [#type]" -> "list weapon skins"))

}
