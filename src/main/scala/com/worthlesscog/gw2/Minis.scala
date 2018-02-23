package com.worthlesscog.gw2

import Utils.noneOrString
import spray.json.{ DefaultJsonProtocol, JsValue }

case class Mini(
        id: Int,
        name: String,
        unlock: Option[String],
        icon: String,
        order: Int,
        item_id: Int,
        collection: Option[String]) extends Collected[Mini] with Id[Int] with Mappable with Named {

    def inCollection(s: String) = copy(collection = Some(s))

    def toMap = Map(
        "id" -> id.toString,
        "name" -> name,
        "unlock" -> noneOrString(unlock),
        "icon" -> icon,
        "order" -> order.toString,
        "item_id" -> item_id.toString,
        "set" -> noneOrString(collection))

    override def toString = name
}

object MiniProtocols extends DefaultJsonProtocol {

    implicit val fmtMini = jsonFormat7(Mini)

}

object Minis extends PersistentMapCatalog[Int, Mini] with IntIdCatalog {

    import MiniProtocols._

    val name = "minis"
    val url = root + "/minis"

    def bulkConvert(v: JsValue) = v.convertTo[List[Mini]]

}
