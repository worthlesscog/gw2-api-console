package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.{ noneOrSorted, noneOrString }
import spray.json.{ DefaultJsonProtocol, JsValue }

case class Recipe(
        chat_link: String,
        disciplines: Set[String],
        flags: Set[String],
        guild_ingredients: Option[List[UpgradeCount]],
        id: Int,
        ingredients: List[ItemCount],
        min_rating: Int,
        output_item_count: Int,
        output_item_id: Int,
        output_upgrade_id: Option[Int],
        time_to_craft_ms: Int,
        `type`: String) extends Flagged with Id[Int] with Mappable with Typed {

    def gi = guild_ingredients.fold("None") { _.map { uc => uc.count + " x #" + uc.upgrade_id } mkString ", " }
    def il = ingredients map { ic => ic.count + " x #" + ic.item_id } mkString ", "

    def toMap = Map(
        "chat_link" -> chat_link,
        "disciplines" -> noneOrSorted(disciplines),
        "flags" -> noneOrSorted(flags),
        "guild_ingredients" -> gi,
        "id" -> id.toString,
        "ingredients" -> il,
        "min_rating" -> min_rating.toString,
        "output_item_count" -> output_item_count.toString,
        "output_item_id" -> output_item_id.toString,
        "output_upgrade_id" -> noneOrString(output_upgrade_id),
        "time_to_craft_ms" -> time_to_craft_ms.toString,
        "type" -> `type`)
}

case class ItemCount(item_id: Int, count: Int)

case class UpgradeCount(upgrade_id: Int, count: Int)

object RecipeProtocols extends DefaultJsonProtocol {

    implicit val fmtUpgradeCount = jsonFormat2(UpgradeCount)
    implicit val fmtItemCount = jsonFormat2(ItemCount)
    implicit val fmtRecipe = jsonFormat12(Recipe)

}

object Recipes extends PersistentMapCatalog[Int, Recipe] with IntIdCatalog {

    import RecipeProtocols._

    val name = "recipes"
    val url = root + "/recipes"

    def bulkConvert(v: JsValue) = v.convertTo[List[Recipe]]

}
