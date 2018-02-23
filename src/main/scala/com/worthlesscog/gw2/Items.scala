package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.{ noneOrCommas, noneOrSorted, noneOrString, toStringPrice }
import spray.json.{ DefaultJsonProtocol, JsString, JsValue, RootJsonFormat, pimpAny }

// XXX - needs specific unmarshall

trait Item extends FlagNameTypeAndMap with Id[Int] {
    def chat_link: String
    def default_skin: Option[Int]
    def description: Option[String]
    def flags: Set[String]
    def game_types: Set[String]
    def icon: Option[String]
    def id: Int
    def level: Int
    def name: String
    def rarity: String
    def restrictions: Set[String]
    def `type`: String
    def vendor_value: Int
    def buy: Option[Int]
    def sell: Option[Int]

    def l = if (level > 0) s"L$level " else ""
    def t = `type`

    def toMap = Map(
        "chat_link" -> chat_link,
        "description" -> noneOrString(description),
        "default_skin" -> noneOrString(default_skin),
        "flags" -> noneOrSorted(flags),
        "game_types" -> noneOrSorted(game_types),
        "icon" -> noneOrString(icon),
        "id" -> id.toString,
        "level" -> level.toString,
        "name" -> name,
        "rarity" -> rarity,
        "restrictions" -> noneOrSorted(restrictions),
        "type" -> `type`,
        "vendor_value" -> toStringPrice(vendor_value))

    override def toString = s"$name, $rarity, $l$t"
}

case class Armor(
        id: Int,
        chat_link: String,
        name: String,
        icon: Option[String],
        description: Option[String],
        `type`: String,
        rarity: String,
        level: Int,
        vendor_value: Int,
        default_skin: Option[Int],
        flags: Set[String],
        game_types: Set[String],
        restrictions: Set[String],
        details: ArmorDetails,
        buy: Option[Int],
        sell: Option[Int]) extends Detailed with Item {

    override def t = details.`type`
    def w = details.weight_class

    override def toMap = super.toMap ++ details.toMap
    override def toString = s"$name, $rarity, $l$w $t"
}

case class ArmorDetails(
        `type`: String,
        weight_class: String,
        defense: Int,
        infusion_slots: List[InfusionSlot],
        infix_upgrade: Option[InfixUpgrade],
        suffix_item_id: Option[Int],
        secondary_suffix_item_id: String,
        stat_choices: Option[List[Int]]) extends Details with Mappable {

    def is = infusion_slots map { _.toString } mkString ", "
    def iu = noneOrString(infix_upgrade)

    def toMap = Map(
        "armor_type" -> `type`,
        "weight_class" -> weight_class,
        "defense" -> defense.toString,
        "infusion_slots" -> is,
        "infix_upgrade" -> iu,
        "suffix_item_id" -> noneOrString(suffix_item_id),
        "secondary_suffix_item_id" -> secondary_suffix_item_id,
        "stat_choices" -> noneOrCommas(stat_choices))
}

case class InfusionSlot(flags: List[String], item_id: Option[Int]) {
    override def toString = flags.mkString(", ") + item_id.fold("") { " " + }
}

case class InfixUpgrade(attributes: List[AttributeModifier], buff: Option[Buff], id: Int) {
    def al = attributes map { _.toString } mkString (", ", ", ", "")

    override def toString = id + al + buff.fold("") { ", " + }
}

case class AttributeModifier(attribute: String, modifier: Int) {
    override def toString = attribute + " " + modifier
}

case class Buff(skill_id: Int, description: Option[String]) {
    override def toString = description.fold(skill_id.toString) { skill_id + " " + }
}

case class Back(
    id: Int,
    chat_link: String,
    name: String,
    icon: Option[String],
    description: Option[String],
    `type`: String,
    rarity: String,
    level: Int,
    vendor_value: Int,
    default_skin: Option[Int],
    flags: Set[String],
    game_types: Set[String],
    restrictions: Set[String],
    details: BackDetails,
    buy: Option[Int],
    sell: Option[Int]) extends Item

case class BackDetails(
    infusion_slots: List[InfusionSlot],
    infix_upgrade: Option[InfixUpgrade],
    suffix_item_id: Option[Int],
    secondary_suffix_item_id: String,
    stat_choices: Option[List[Int]])

case class Bag(
        id: Int,
        chat_link: String,
        name: String,
        icon: Option[String],
        description: Option[String],
        `type`: String,
        rarity: String,
        level: Int,
        vendor_value: Int,
        default_skin: Option[Int],
        flags: Set[String],
        game_types: Set[String],
        restrictions: Set[String],
        details: BagDetails,
        buy: Option[Int],
        sell: Option[Int]) extends Item {

    override def toString = s"$name, $rarity $l${details.size} slot $t"
}

case class BagDetails(size: Int, no_sell_or_sort: Boolean)

case class Consumable(
        id: Int,
        chat_link: String,
        name: String,
        icon: Option[String],
        description: Option[String],
        `type`: String,
        rarity: String,
        level: Int,
        vendor_value: Int,
        default_skin: Option[Int],
        flags: Set[String],
        game_types: Set[String],
        restrictions: Set[String],
        details: ConsumableDetails,
        buy: Option[Int],
        sell: Option[Int]) extends Detailed with Item {

    override def t = details.`type`
}

case class ConsumableDetails(
    `type`: String,
    description: Option[String],
    duration_ms: Option[Int],
    unlock_type: Option[String],
    color_id: Option[Int],
    recipe_id: Option[Int],
    apply_count: Option[Int],
    name: Option[String]) extends Details

case class Container(
    id: Int,
    chat_link: String,
    name: String,
    icon: Option[String],
    description: Option[String],
    `type`: String,
    rarity: String,
    level: Int,
    vendor_value: Int,
    default_skin: Option[Int],
    flags: Set[String],
    game_types: Set[String],
    restrictions: Set[String],
    details: ContainerDetails,
    buy: Option[Int],
    sell: Option[Int]) extends Detailed with Item

case class ContainerDetails(`type`: String) extends Details

case class CraftingMaterial(
    id: Int,
    chat_link: String,
    name: String,
    icon: Option[String],
    description: Option[String],
    `type`: String,
    rarity: String,
    level: Int,
    vendor_value: Int,
    default_skin: Option[Int],
    flags: Set[String],
    game_types: Set[String],
    restrictions: Set[String],
    buy: Option[Int],
    sell: Option[Int]) extends Item

case class GatheringTool(
        id: Int,
        chat_link: String,
        name: String,
        icon: Option[String],
        description: Option[String],
        `type`: String,
        rarity: String,
        level: Int,
        vendor_value: Int,
        default_skin: Option[Int],
        flags: Set[String],
        game_types: Set[String],
        restrictions: Set[String],
        details: GatheringToolDetails,
        buy: Option[Int],
        sell: Option[Int]) extends Detailed with Item {

    override def t = details.`type`
}

case class GatheringToolDetails(`type`: String) extends Details

case class Gizmo(
    id: Int,
    chat_link: String,
    name: String,
    icon: Option[String],
    description: Option[String],
    `type`: String,
    rarity: String,
    level: Int,
    vendor_value: Int,
    default_skin: Option[Int],
    flags: Set[String],
    game_types: Set[String],
    restrictions: Set[String],
    details: GizmoDetails,
    buy: Option[Int],
    sell: Option[Int]) extends Detailed with Item

case class GizmoDetails(`type`: String) extends Details

case class Key(
    id: Int,
    chat_link: String,
    name: String,
    icon: Option[String],
    description: Option[String],
    `type`: String,
    rarity: String,
    level: Int,
    vendor_value: Int,
    default_skin: Option[Int],
    flags: Set[String],
    game_types: Set[String],
    restrictions: Set[String],
    buy: Option[Int],
    sell: Option[Int]) extends Item

case class MiniPet(
    id: Int,
    chat_link: String,
    name: String,
    icon: Option[String],
    description: Option[String],
    `type`: String,
    rarity: String,
    level: Int,
    vendor_value: Int,
    default_skin: Option[Int],
    flags: Set[String],
    game_types: Set[String],
    restrictions: Set[String],
    details: MiniPetDetails,
    buy: Option[Int],
    sell: Option[Int]) extends Item

case class MiniPetDetails(minipet_id: Int)

case class Tool(
        id: Int,
        chat_link: String,
        name: String,
        icon: Option[String],
        description: Option[String],
        `type`: String,
        rarity: String,
        level: Int,
        vendor_value: Int,
        default_skin: Option[Int],
        flags: Set[String],
        game_types: Set[String],
        restrictions: Set[String],
        details: ToolDetails,
        buy: Option[Int],
        sell: Option[Int]) extends Detailed with Item {

    override def t = details.`type`
}

case class ToolDetails(`type`: String, charges: Int) extends Details

case class Trait(
    id: Int,
    chat_link: String,
    name: String,
    icon: Option[String],
    description: Option[String],
    `type`: String,
    rarity: String,
    level: Int,
    vendor_value: Int,
    default_skin: Option[Int],
    flags: Set[String],
    game_types: Set[String],
    restrictions: Set[String],
    buy: Option[Int],
    sell: Option[Int]) extends Item

case class Trinket(
        id: Int,
        chat_link: String,
        name: String,
        icon: Option[String],
        description: Option[String],
        `type`: String,
        rarity: String,
        level: Int,
        vendor_value: Int,
        default_skin: Option[Int],
        flags: Set[String],
        game_types: Set[String],
        restrictions: Set[String],
        details: TrinketDetails,
        buy: Option[Int],
        sell: Option[Int]) extends Detailed with Item {

    override def t = details.`type`
}

case class TrinketDetails(
    `type`: String,
    infusion_slots: List[InfusionSlot],
    infix_upgrade: Option[InfixUpgrade],
    suffix_item_id: Option[Int],
    secondary_suffix_item_id: String,
    stat_choices: Option[List[Int]]) extends Details

case class Trophy(
    id: Int,
    chat_link: String,
    name: String,
    icon: Option[String],
    description: Option[String],
    `type`: String,
    rarity: String,
    level: Int,
    vendor_value: Int,
    default_skin: Option[Int],
    flags: Set[String],
    game_types: Set[String],
    restrictions: Set[String],
    buy: Option[Int],
    sell: Option[Int]) extends Item

case class UpgradeComponent(
        id: Int,
        chat_link: String,
        name: String,
        icon: Option[String],
        description: Option[String],
        `type`: String,
        rarity: String,
        level: Int,
        vendor_value: Int,
        default_skin: Option[Int],
        flags: Set[String],
        game_types: Set[String],
        restrictions: Set[String],
        details: UpgradeComponentDetails,
        buy: Option[Int],
        sell: Option[Int]) extends Detailed with Item {

    override def t = details.`type`
}

case class UpgradeComponentDetails(
    `type`: String,
    flags: List[String],
    infusion_upgrade_flags: List[String],
    suffix: String,
    infix_upgrade: UpgradeInfixUpgrade,
    bonuses: Option[List[String]]) extends Details

case class UpgradeInfixUpgrade(
    id: Int,
    attributes: List[AttributeModifier],
    buff: Option[Buff])

case class Weapon(
        id: Int,
        chat_link: String,
        name: String,
        icon: Option[String],
        description: Option[String],
        `type`: String,
        rarity: String,
        level: Int,
        vendor_value: Int,
        default_skin: Option[Int],
        flags: Set[String],
        game_types: Set[String],
        restrictions: Set[String],
        details: WeaponDetails,
        buy: Option[Int],
        sell: Option[Int]) extends Detailed with Item {

    override def t = details.`type`
}

case class WeaponDetails(
    `type`: String,
    damage_type: String,
    min_power: Int,
    max_power: Int,
    defense: Int,
    infusion_slots: List[InfusionSlot],
    infix_upgrade: Option[InfixUpgrade],
    suffix_item_id: Option[Int],
    secondary_suffix_item_id: String,
    stat_choices: Option[List[Int]]) extends Details

object ItemProtocols extends DefaultJsonProtocol {

    implicit val fmtBuff = jsonFormat2(Buff)
    implicit val fmtAttributeModifier = jsonFormat2(AttributeModifier)
    implicit val fmtInfixUpgrade = jsonFormat3(InfixUpgrade)
    implicit val fmtInfusionSlot = jsonFormat2(InfusionSlot)
    implicit val fmtArmorDetails = jsonFormat8(ArmorDetails)
    implicit val fmtArmor = jsonFormat16(Armor)

    implicit val fmtBackDetails = jsonFormat5(BackDetails)
    implicit val fmtBack = jsonFormat16(Back)

    implicit val fmtBagDetails = jsonFormat2(BagDetails)
    implicit val fmtBag = jsonFormat16(Bag)

    implicit val fmtConsumableDetails = jsonFormat8(ConsumableDetails)
    implicit val fmtConsumable = jsonFormat16(Consumable)

    implicit val fmtContainerDetails = jsonFormat1(ContainerDetails)
    implicit val fmtContainer = jsonFormat16(Container)

    implicit val fmtCraftingMaterial = jsonFormat15(CraftingMaterial)

    implicit val fmtGatheringDetails = jsonFormat1(GatheringToolDetails)
    implicit val fmtGathering = jsonFormat16(GatheringTool)

    implicit val fmtGizmoDetails = jsonFormat1(GizmoDetails)
    implicit val fmtGizmo = jsonFormat16(Gizmo)

    implicit val fmtKey = jsonFormat15(Key)

    implicit val fmtMiniPetDetails = jsonFormat1(MiniPetDetails)
    implicit val fmtMiniPet = jsonFormat16(MiniPet)

    implicit val fmtToolDetails = jsonFormat2(ToolDetails)
    implicit val fmtTool = jsonFormat16(Tool)

    implicit val fmtTrait = jsonFormat15(Trait)

    implicit val fmtTrinketDetails = jsonFormat6(TrinketDetails)
    implicit val fmtTrinket = jsonFormat16(Trinket)

    implicit val fmtTrophy = jsonFormat15(Trophy)

    implicit val fmtUpgradeInfixUpgrade = jsonFormat3(UpgradeInfixUpgrade)
    implicit val fmtUpgradeComponentDetails = jsonFormat6(UpgradeComponentDetails)
    implicit val fmtUpgradeComponent = jsonFormat16(UpgradeComponent)

    implicit val fmtWeaponDetails = jsonFormat10(WeaponDetails)
    implicit val fmtWeapon = jsonFormat16(Weapon)

    implicit object ItemFormat extends RootJsonFormat[Item] {
        def write(i: Item) = i.toJson

        def read(v: JsValue) = v.asJsObject.getFields("type") match {
            case Seq(JsString("Armor"))            => v.convertTo[Armor]
            case Seq(JsString("Back"))             => v.convertTo[Back]
            case Seq(JsString("Bag"))              => v.convertTo[Bag]
            case Seq(JsString("Consumable"))       => v.convertTo[Consumable]
            case Seq(JsString("Container"))        => v.convertTo[Container]
            case Seq(JsString("CraftingMaterial")) => v.convertTo[CraftingMaterial]
            case Seq(JsString("Gathering"))        => v.convertTo[GatheringTool]
            case Seq(JsString("Gizmo"))            => v.convertTo[Gizmo]
            case Seq(JsString("Key"))              => v.convertTo[Key]
            case Seq(JsString("MiniPet"))          => v.convertTo[MiniPet]
            case Seq(JsString("Tool"))             => v.convertTo[Tool]
            case Seq(JsString("Trait"))            => v.convertTo[Trait]
            case Seq(JsString("Trinket"))          => v.convertTo[Trinket]
            case Seq(JsString("Trophy"))           => v.convertTo[Trophy]
            case Seq(JsString("UpgradeComponent")) => v.convertTo[UpgradeComponent]
            case Seq(JsString("Weapon"))           => v.convertTo[Weapon]
        }
    }

}

object Items extends PersistentMapCatalog[Int, Item] with IntIdCatalog {

    import ItemProtocols._

    val name = "items"
    val url = root + "/items"

    def bulkConvert(v: JsValue) = v.convertTo[List[Item]]

}
