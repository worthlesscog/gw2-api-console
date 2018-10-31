package com.worthlesscog.gw2

import Utils.optLabelledFloat
import spray.json.{ DefaultJsonProtocol, JsValue }

case class ItemStatSet(
        id: Int,
        name: String,
        attributes: Option[List[Attribute]]) extends Id[Int] with Mappable with Named {

    def attrs = attributes.fold("") { _.mkString(", ") }

    def toMap = Map(
        "id" -> id.toString,
        "name" -> name,
        "attributes" -> attrs)

    override def toString = name

}

case class Attribute(
        attribute: String,
        multiplier: Float,
        value: Int) {

    override def toString = attribute + " multiplier " + multiplier + " / value " + value

}

object ItemStatProtocols extends DefaultJsonProtocol {

    implicit val fmtAttribute = jsonFormat3(Attribute)
    implicit val fmtItemStats = jsonFormat3(ItemStatSet)

}

object ItemStats extends PersistentMapCatalog[Int, ItemStatSet] with IntIdCatalog {

    import ItemStatProtocols._

    val name = "item stats"
    val url = root + "/itemstats"

    def bulkConvert(v: JsValue) = v.convertTo[List[ItemStatSet]]

}
