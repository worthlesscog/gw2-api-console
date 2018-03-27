package com.worthlesscog.gw2

import spray.json.{ DefaultJsonProtocol, JsValue }

case class AchievementCategory(
        id: Int,
        name: String,
        description: String,
        order: Int,
        icon: String,
        achievements: List[Int]) extends Id[Int] with Named {

    override def toString = name

}

object AchievementCategoryProtocols extends DefaultJsonProtocol {

    implicit val fmtAchievementCategory = jsonFormat6(AchievementCategory)

}

object AchievementCategories extends PersistentMapCatalog[Int, AchievementCategory] with IntIdCatalog {

    import AchievementCategoryProtocols._

    val name = "achievement categories"
    val url = root + "/achievements/categories"

    def bulkConvert(v: JsValue) = v.convertTo[List[AchievementCategory]]

}
