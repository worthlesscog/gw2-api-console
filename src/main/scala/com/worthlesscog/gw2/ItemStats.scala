package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.optLabelledFloat
import spray.json.{ DefaultJsonProtocol, JsValue }

case class ItemStatSet(id: Int, name: String, attributes: Attributes) extends Id[Int] with Mappable with Named {
    def toMap = Map(
        "id" -> id.toString,
        "attributes" -> attributes.toString,
        "name" -> name)

    override def toString = name
}

case class Attributes(
        AgonyResistance: Option[Float],
        BoonDuration: Option[Float],
        ConditionDamage: Option[Float],
        ConditionDuration: Option[Float],
        CritDamage: Option[Float],
        Healing: Option[Float],
        Power: Option[Float],
        Precision: Option[Float],
        Toughness: Option[Float],
        Vitality: Option[Float]) {

    def agony = optLabelledFloat(AgonyResistance, "Agony Resistance")
    def boon = optLabelledFloat(BoonDuration, "Boon Duration")
    def condi = optLabelledFloat(ConditionDamage, "Condition Damage")
    def dur = optLabelledFloat(ConditionDuration, "Condition Duration")
    def crit = optLabelledFloat(CritDamage, "Crit Damage")
    def heal = optLabelledFloat(Healing, "Healing")
    def power = optLabelledFloat(Power, "Power")
    def prec = optLabelledFloat(Precision, "Precision")
    def tough = optLabelledFloat(Toughness, "Toughness")
    def vit = optLabelledFloat(Vitality, "Vitality")

    override def toString = List(agony, boon, condi, dur, crit, heal, power, prec, tough, vit).flatten mkString ", "
}

object ItemStatProtocols extends DefaultJsonProtocol {

    implicit val fmtAttributes = jsonFormat10(Attributes)
    implicit val fmtItemStats = jsonFormat3(ItemStatSet)

}

object ItemStats extends PersistentMapCatalog[Int, ItemStatSet] with IntIdCatalog {

    import ItemStatProtocols._

    val name = "itemstats"
    val url = root + "/itemstats"

    def bulkConvert(v: JsValue) = v.convertTo[List[ItemStatSet]]

}
