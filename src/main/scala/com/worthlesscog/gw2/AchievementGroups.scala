package com.worthlesscog.gw2

import spray.json.{ DefaultJsonProtocol, JsValue }

case class AchievementGroup(
        id: String,
        name: String,
        description: String,
        order: Int,
        categories: Set[Int]) extends Id[String] with Named {

    override def toString = name
}

object AchievementGroupProtocols extends DefaultJsonProtocol {

    implicit val fmtAchievementGroup = jsonFormat5(AchievementGroup)

}

object AchievementGroups extends PersistentMapCatalog[String, AchievementGroup] with StringIdCatalog {

    import AchievementGroupProtocols._

    // XXX - these keys are loooong
    override val chunks = 2

    val name = "achievement groups"
    val url = root + "/achievements/groups"

    def bulkConvert(v: JsValue) = v.convertTo[List[AchievementGroup]]

}
