package com.worthlesscog.gw2

import spray.json.{ DefaultJsonProtocol, JsValue }

case class AccountAchievement(
    id: Int,
    current: Option[Int],
    max: Option[Int],
    done: Boolean,
    repeated: Option[Int],
    bits: Option[List[Int]]) extends Id[Int]

object AccountAchievementProtocols extends DefaultJsonProtocol {

    implicit val fmtAccountAchievement = jsonFormat6(AccountAchievement)

}

object AccountAchievements extends BlobCatalog[AccountAchievement] {

    import AccountAchievementProtocols._

    val name = "account achievements"
    val url = root + "/account/achievements"

    def bulkConvert(v: JsValue) = v.convertTo[List[AccountAchievement]]

}
