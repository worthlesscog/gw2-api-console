package com.worthlesscog.gw2

import Utils.noneOrString
import spray.json.{DefaultJsonProtocol, JsValue}

case class Mount(
    id: Int,
    name: String,
    icon: String,
    mount: String,
    // dye_slots:
    collection: Option[String]) extends Collected[Mount] with Id[Int] with Mappable with Named with Typed {

    def inCollection(s: String) = copy(collection = Some(s))

    def toMap = Map(
        "id" -> id.toString,
        "name" -> name,
        "icon" -> icon,
        "mount" -> mount,
        "collection" -> noneOrString(collection))

    def `type` = mount

    override def toString = name

}

object MountProtocols extends DefaultJsonProtocol {

    implicit val fmtMount = jsonFormat5(Mount)

}

object Mounts extends PersistentMapCatalog[Int, Mount] with IntIdCatalog {

    import MountProtocols._

    val name = "mounts"
    val url = root + "/mounts/skins"

    def bulkConvert(v: JsValue) = v.convertTo[List[Mount]]

}
