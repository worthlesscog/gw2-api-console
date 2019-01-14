package com.worthlesscog.gw2

import Utils.{noneOrCommas, noneOrString}
import spray.json.{DefaultJsonProtocol, JsValue}

case class Title(
    id: Int,
    name: String,
    achievement: Option[Int],
    achievements: Option[List[Int]],
    ap_required: Option[Int]) extends Id[Int] with Mappable with Named {

    def toMap = Map(
        "id" -> id.toString,
        "name" -> name,
        "achievement" -> noneOrString(achievement),
        "achievements" -> noneOrCommas(achievements),
        "ap_required" -> noneOrString(ap_required))

    override def toString = name

}

object TitleProtocols extends DefaultJsonProtocol {

    implicit val fmtTitle = jsonFormat5(Title)

}

object Titles extends PersistentMapCatalog[Int, Title] with IntIdCatalog {

    import TitleProtocols._

    val name = "titles"
    val url = root + "/titles"

    def bulkConvert(v: JsValue) = v.convertTo[List[Title]]

}
