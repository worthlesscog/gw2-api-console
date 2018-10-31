package com.worthlesscog.gw2

import scala.reflect.ClassTag

import Utils.{ byName, dumpAndTally, isNumeric, priceItems, priceMinis, priceSkins, toMap }

class CollectionsCommand extends AchievementsCommand("collections") {

    override def execute(cmd: List[String]): Unit = cmd match {
        case "nearly" :: Nil =>
            collections |> started |> incomplete |> dumpAndTally(nearly, completed, 50)

        case i :: Nil if (i |> isNumeric) =>
            collections.get(i.toInt) foreach priceCollection

        case Nil =>
            collections |> dumpAndTally(byName, completed)

        case _ =>
            execute(cmd, collections, achievementFlags, achievementTypes)
    }

    def priceCollection(a: Achievement) = {
        a.bits foreach { b =>
            val is = select[ItemProgress](b) flatMap { _.id flatMap items.get } filter { _.isUnpriced }
            val ps = select[MinipetProgress](b) flatMap { _.id flatMap minis.get } filter { _.isUnpriced }
            val ss = select[SkinProgress](b) flatMap { _.id flatMap skins.get } filter { _.isUnpriced }
            is |> toMap |> priceItems
            minis = minis ++ (ps |> toMap |> priceMinis)
            skins = skins ++ (ss |> toMap |> priceSkins)
        }
        dumpObject(a)
    }

    def select[T](i: List[_ >: T])(implicit tag: ClassTag[T]) =
        i flatMap {
            case t: T => Some(t)
            case _    => None
        }

    override def uses = Some(Map(s"collections [$ID_CONT_FLAG_TYPE | nearly]" -> s"list achievement collections"))

}
