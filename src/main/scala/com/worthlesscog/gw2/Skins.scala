package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.{ noneOrSorted, noneOrString, noneOrStrings, splitAndBar }
import spray.json.{ DefaultJsonProtocol, JsString, JsValue, NullOptions, RootJsonFormat, pimpAny }

trait Skin extends FlagNameTypeAndMap with Collected[Skin] with Id[Int] with Priced[Skin] {
    def id: Int
    def name: String
    def `type`: String
    def flags: Set[String]
    def restrictions: Set[String]
    def icon: Option[String]
    def rarity: String
    def description: Option[String]
    def collection: Option[String]
    def buy: Option[Int]
    def sell: Option[Int]

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
        collection: Option[String],
        buy: Option[Int],
        sell: Option[Int]) extends Detailed with Skin {

    // XXX - :/
    def inCollection(s: String) = copy(collection = Some(s))

    override def toMap = super.toMap ++ details.toMap

    def withPrices(b: Option[Int], s: Option[Int]) =
        copy(buy = b, sell = s)

}

case class ArmorSkinDetails(
        `type`: String,
        weight_class: String,
        dye_slots: Option[DyeSlots]) extends Details with Mappable {

    def ds = dye_slots.fold(Map("dye_slots" -> "None"))(_.toMap)

    def toMap = Map(
        "armor_type" -> `type`,
        "weight_class" -> weight_class) ++ ds

}

case class DyeSlots(
        `default`: List[Option[ColorMaterial]],
        overrides: Map[String, List[Option[ColorMaterial]]]) extends Mappable {

    def orides = overrides map { case (k, v) => ("dye_slot_override_" + splitAndBar(k)) -> noneOrStrings(v) }

    def toMap = Map(
        "dye_slot_defaults" -> noneOrStrings(`default`)) ++ orides

}

case class ColorMaterial(
        color_id: Int,
        material: String) {

    override def toString = s"color_id $color_id / material $material"

}

case class BackSkin(
        id: Int,
        name: String,
        `type`: String,
        flags: Set[String],
        restrictions: Set[String],
        icon: Option[String],
        rarity: String,
        description: Option[String],
        collection: Option[String],
        buy: Option[Int],
        sell: Option[Int]) extends Skin {

    // XXX - :/
    def inCollection(s: String) = copy(collection = Some(s))

    def withPrices(b: Option[Int], s: Option[Int]) =
        copy(buy = b, sell = s)

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
        details: GatheringToolSkinDetails,
        buy: Option[Int],
        sell: Option[Int]) extends Detailed with Skin {

    // XXX - :/
    def inCollection(s: String) = copy(collection = Some(s))

    def withPrices(b: Option[Int], s: Option[Int]) =
        copy(buy = b, sell = s)

}

case class GatheringToolSkinDetails(
    `type`: String) extends Details

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
        details: WeaponSkinDetails,
        buy: Option[Int],
        sell: Option[Int]) extends Detailed with Skin {

    // XXX - :/
    def inCollection(s: String) = copy(collection = Some(s))

    def withPrices(b: Option[Int], s: Option[Int]) =
        copy(buy = b, sell = s)

}

case class WeaponSkinDetails(
    `type`: String,
    damage_type: String) extends Details

object SkinProtocols extends DefaultJsonProtocol with NullOptions {

    implicit val fmtColorMaterial = jsonFormat2(ColorMaterial)
    implicit val fmtDyeSlots = jsonFormat2(DyeSlots)
    implicit val fmtArmorSkinDetails = jsonFormat3(ArmorSkinDetails)
    implicit val fmtArmorSkin = jsonFormat12(ArmorSkin)

    implicit val fmtBackSkin = jsonFormat11(BackSkin)

    implicit val fmtGatheringToolSkinDetails = jsonFormat1(GatheringToolSkinDetails)
    implicit val fmtGatheringToolSkin = jsonFormat12(GatheringToolSkin)

    implicit val fmtWeaponSkinDetails = jsonFormat2(WeaponSkinDetails)
    implicit val fmtWeaponSkin = jsonFormat12(WeaponSkin)

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
