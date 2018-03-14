package com.worthlesscog.gw2

import spray.json.{ DefaultJsonProtocol, JsValue }

case class Race(
        id: String,
        name: String,
        skills: Set[Int]) extends Id[String] with Named {

    override def toString = id
}

object RaceProtocols extends DefaultJsonProtocol {

    implicit val fmtAchievementGroup = jsonFormat3(Race)

}

object Races extends PersistentMapCatalog[String, Race] with StringIdCatalog {

    import RaceProtocols._

    val name = "races"
    val url = root + "/races"

    def bulkConvert(v: JsValue) = v.convertTo[List[Race]]

}
