package com.worthlesscog.gw2

import spray.json.{DefaultJsonProtocol, JsValue}

case class Mastery(
    id: Int,
    name: String,
    requirement: String,
    order: Int,
    background: String,
    region: String,
    levels: List[MasteryLevel]) extends Id[Int] with Named {

    override def toString = name

}

case class MasteryLevel(
    name: String,
    description: String,
    instruction: String,
    icon: String,
    point_cost: Int,
    exp_cost: Int)

object MasteryProtocols extends DefaultJsonProtocol {

    implicit val fmtMasteryLevel = jsonFormat6(MasteryLevel)
    implicit val fmtMastery = jsonFormat7(Mastery)

}

object Masteries extends PersistentMapCatalog[Int, Mastery] with IntIdCatalog {

    import MasteryProtocols._

    val name = "masteries"
    val url = root + "/masteries"

    def bulkConvert(v: JsValue) = v.convertTo[List[Mastery]]

}
