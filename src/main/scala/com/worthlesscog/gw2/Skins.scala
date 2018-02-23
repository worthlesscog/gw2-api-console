package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.{ noneOrSorted, noneOrString }
import spray.json.{ DefaultJsonProtocol, JsString, JsValue, NullOptions, RootJsonFormat, pimpAny }

trait Skin extends FlagNameTypeAndMap with Collected[Skin] with Id[Int] {
    def id: Int
    def name: String
    def `type`: String
    def flags: Set[String]
    def restrictions: Set[String]
    def icon: Option[String]
    def rarity: String
    def description: Option[String]
    def collection: Option[String]

    def n = if (name isEmpty) "" else name + ", "
    def t = `type`

    def toMap = Map(
        "id" -> id.toString,
        "name" -> name,
        "type" -> `type`,
        "flags" -> noneOrSorted(flags),
        "restrictions" -> noneOrSorted(restrictions),
        "icon" -> noneOrString(icon),
        "rarity" -> rarity,
        "description" -> noneOrString(description),
        "collection" -> noneOrString(collection))

    override def toString = s"$n$rarity, $t"
}

case class ArmorSkin(
        id: Int,
        name: String,
        `type`: String,
        flags: Set[String],
        restrictions: Set[String],
        icon: Option[String],
        rarity: String,
        description: Option[String],
        details: ArmorSkinDetails,
        collection: Option[String]) extends Detailed with Skin {

    // XXX - :/
    def inCollection(s: String) = copy(collection = Some(s))
}

case class ArmorSkinDetails(`type`: String, weight_class: String, dye_slots: Option[DyeSlots]) extends Details

case class DyeSlots(`default`: List[Option[ColorMaterial]], overrides: Map[String, List[Option[ColorMaterial]]])

case class ColorMaterial(color_id: Int, material: String)

case class BackSkin(
        id: Int,
        name: String,
        `type`: String,
        flags: Set[String],
        restrictions: Set[String],
        icon: Option[String],
        rarity: String,
        description: Option[String],
        collection: Option[String]) extends Skin {

    // XXX - :/
    def inCollection(s: String) = copy(collection = Some(s))
}

case class GatheringToolSkin(
        id: Int,
        name: String,
        `type`: String,
        flags: Set[String],
        restrictions: Set[String],
        icon: Option[String],
        rarity: String,
        description: Option[String],
        collection: Option[String],
        details: GatheringToolSkinDetails) extends Detailed with Skin {

    // XXX - :/
    def inCollection(s: String) = copy(collection = Some(s))
}

case class GatheringToolSkinDetails(`type`: String) extends Details

case class WeaponSkin(
        id: Int,
        name: String,
        `type`: String,
        flags: Set[String],
        restrictions: Set[String],
        icon: Option[String],
        rarity: String,
        description: Option[String],
        collection: Option[String],
        details: WeaponSkinDetails) extends Detailed with Skin {

    // XXX - :/
    def inCollection(s: String) = copy(collection = Some(s))
}

case class WeaponSkinDetails(`type`: String, damage_type: String) extends Details

object SkinProtocols extends DefaultJsonProtocol with NullOptions {

    implicit val fmtColorMaterial = jsonFormat2(ColorMaterial)
    implicit val fmtDyeSlots = jsonFormat2(DyeSlots)
    implicit val fmtArmorSkinDetails = jsonFormat3(ArmorSkinDetails)
    implicit val fmtArmorSkin = jsonFormat10(ArmorSkin)

    implicit val fmtBackSkin = jsonFormat9(BackSkin)

    implicit val fmtGatheringToolSkinDetails = jsonFormat1(GatheringToolSkinDetails)
    implicit val fmtGatheringToolSkin = jsonFormat10(GatheringToolSkin)

    implicit val fmtWeaponSkinDetails = jsonFormat2(WeaponSkinDetails)
    implicit val fmtWeaponSkin = jsonFormat10(WeaponSkin)

    implicit object SkinFormat extends RootJsonFormat[Skin] {
        def write(i: Skin) = i.toJson

        def read(v: JsValue) = v.asJsObject.getFields("type") match {
            case Seq(JsString("Armor"))     => v.convertTo[ArmorSkin]
            case Seq(JsString("Back"))      => v.convertTo[BackSkin]
            case Seq(JsString("Gathering")) => v.convertTo[GatheringToolSkin]
            case Seq(JsString("Weapon"))    => v.convertTo[WeaponSkin]
        }
    }
}

object Skins extends PersistentMapCatalog[Int, Skin] with IntIdCatalog {

    import SkinProtocols._

    val name = "skins"
    val url = root + "/skins"

    def bulkConvert(v: JsValue) = v.convertTo[List[Skin]]

}
