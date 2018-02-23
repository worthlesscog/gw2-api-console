package com.worthlesscog.gw2

import Utils.{ asString, byName, dumpAndTally, ofDetailType, ofWeight }

abstract class LockedOrUnlocked extends Command {

    def execute(cmd: List[String], select: Set[Int] => Map[Int, Skin] => Map[Int, Skin], detail: Option[Skin => List[String]]): Unit = cmd match {
        case w :: t :: Nil =>
            armorSkins |> ofWeight(w) |> ofDetailType(t) |> select(accountSkins) |> dumpAndTally(byName, asString) //, detail)

        case wt :: Nil =>
            if (armorWeights contains wt)
                armorSkins |> ofWeight(wt) |> select(accountSkins) |> dumpAndTally(byName, asString) //, detail)
            else if (armorTypes contains wt)
                armorSkins |> ofDetailType(wt) |> select(accountSkins) |> dumpAndTally(byName, asString) //, detail)
            else if (weaponTypes contains wt)
                weaponSkins |> ofDetailType(wt) |> select(accountSkins) |> dumpAndTally(byName, asString) //, detail)

        case Nil =>
            skins |> select(accountSkins) |> dumpAndTally(byName, asString) //, detail)

        case _ =>
    }

}
