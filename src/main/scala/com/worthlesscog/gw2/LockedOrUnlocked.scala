package com.worthlesscog.gw2

import Utils.{ asString, byName, dumpAndTally, ofDetailType, ofWeight }

abstract class LockedOrUnlocked extends Command {

    def execute(cmd: List[String], select: Set[Int] => Map[Int, Skin] => Map[Int, Skin], detail: Option[Skin => List[String]]): Unit = cmd match {
        case w :: t :: Nil =>
            armorSkins |> ofWeight(w) |> ofDetailType(t) |> select(accountSkins) |> dumpAndTally(byName, asString) //, detail)

        case rwt :: Nil =>
            if (raceNames contains rwt) {
                armorSkins |> forRace(rwt) |> select(accountSkins) |> dumpAndTally(byName, asString)
            } else if (armorWeights contains rwt)
                armorSkins |> ofWeight(rwt) |> select(accountSkins) |> dumpAndTally(byName, asString) //, detail)
            else if (armorTypes contains rwt)
                armorSkins |> ofDetailType(rwt) |> select(accountSkins) |> dumpAndTally(byName, asString) //, detail)
            else if (weaponTypes contains rwt)
                weaponSkins |> ofDetailType(rwt) |> select(accountSkins) |> dumpAndTally(byName, asString) //, detail)

        case Nil =>
            skins |> select(accountSkins) |> dumpAndTally(byName, asString) //, detail)

        case _ =>
    }

    def forRace[K](r: String)(m: Map[K, ArmorSkin]) =
        m filter { case (_, s) => s.restrictions contains r }

}
