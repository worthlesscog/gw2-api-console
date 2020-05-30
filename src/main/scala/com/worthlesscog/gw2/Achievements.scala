package com.worthlesscog.gw2

import spray.json.{pimpAny, DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

case class Achievement(
    id: Int,
    icon: Option[String],
    name: String,
    description: String,
    requirement: String,
    locked_text: String,
    `type`: String,
    flags: Set[String],
    tiers: List[AchievementTier],
    prerequisites: Option[List[Int]],
    rewards: Option[List[AchievementReward]],
    bits: Option[List[AchievementProgress]],
    point_cap: Option[Int]) extends FlagNameTypeAndMap with Id[Int] {

    def isMeta = flags contains "CategoryDisplay"

    def isVisible = !(flags contains "Hidden")

    def toMap = Map()

    override def toString = name + (if (!isVisible) " *" else "")

}

case class AchievementTier(
    count: Int,
    points: Int)

sealed abstract class AchievementReward {
    def `type`: String
}
case class CoinReward(
    `type`: String,
    count: Int) extends AchievementReward
case class ItemReward(
    `type`: String,
    id: Int,
    count: Int) extends AchievementReward
case class MasteryReward(
    `type`: String,
    id: Int,
    region: String) extends AchievementReward
case class TitleReward(
    `type`: String,
    id: Int) extends AchievementReward

sealed abstract class AchievementProgress {
    def `type`: String
}
case class ItemProgress(
    `type`: String,
    id: Option[Int]) extends AchievementProgress
case class MinipetProgress(
    `type`: String,
    id: Option[Int]) extends AchievementProgress
case class SkinProgress(
    `type`: String,
    id: Option[Int]) extends AchievementProgress
case class TextProgress(
    `type`: String,
    text: Option[String]) extends AchievementProgress
case class UnknownProgress() extends AchievementProgress {
    def `type` = "API data bad"
}

object AchievementProtocols extends DefaultJsonProtocol {

    implicit val fmtCoinReward = jsonFormat2(CoinReward)
    implicit val fmtItemReward = jsonFormat3(ItemReward)
    implicit val fmtMasteryReward = jsonFormat3(MasteryReward)
    implicit val fmtTitleReward = jsonFormat2(TitleReward)

    implicit object AchievementRewardFormat extends RootJsonFormat[AchievementReward] {
        def write(o: AchievementReward) = o.toJson

        def read(v: JsValue) = v.asJsObject.getFields("type") match {
            case Seq(JsString("Coins"))   => v.convertTo[CoinReward]
            case Seq(JsString("Item"))    => v.convertTo[ItemReward]
            case Seq(JsString("Mastery")) => v.convertTo[MasteryReward]
            case Seq(JsString("Title"))   => v.convertTo[TitleReward]
        }
    }

    implicit val fmtAchievementTier = jsonFormat2(AchievementTier)

    implicit val fmtItemProgress = jsonFormat2(ItemProgress)
    implicit val fmtMinipetProgress = jsonFormat2(MinipetProgress)
    implicit val fmtSkinProgress = jsonFormat2(SkinProgress)
    implicit val fmtTextProgress = jsonFormat2(TextProgress)
    implicit val fmtUnknownProgress = jsonFormat0(UnknownProgress)

    implicit object AchievementProgressFormat extends RootJsonFormat[AchievementProgress] {
        def write(o: AchievementProgress) = o.toJson

        def read(v: JsValue) =
            v.asJsObject.getFields("type") match {
                case Seq(JsString("Item"))    => v.convertTo[ItemProgress]
                case Seq(JsString("Minipet")) => v.convertTo[MinipetProgress]
                case Seq(JsString("Skin"))    => v.convertTo[SkinProgress]
                case Seq(JsString("Text"))    => v.convertTo[TextProgress]
                case Seq()                    => v.convertTo[UnknownProgress]
            }
    }

    implicit val fmtAchievement = jsonFormat13(Achievement)

}

object Achievements extends PersistentMapCatalog[Int, Achievement] with IntIdCatalog {

    import AchievementProtocols._

    val name = "achievements"
    val url = root + "/achievements"

    def bulkConvert(v: JsValue) = v.convertTo[List[Achievement]]

}
