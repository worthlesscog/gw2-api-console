package com.worthlesscog.gw2

import Utils.{ noneOrSorted, noneOrString }
import spray.json.{ DefaultJsonProtocol, JsValue }

case class Recipe(
        id: Int,
        `type`: String,
        output_item_id: Int,
        output_item_count: Int,
        time_to_craft_ms: Int,
        disciplines: Set[String],
        min_rating: Int,
        flags: Set[String],
        ingredients: List[ItemCount],
        guild_ingredients: Option[List[UpgradeCount]],
        output_upgrade_id: Option[Int],
        chat_link: String,
        buy: Option[Int],
        sell: Option[Int]) extends Flagged with Id[Int] with Mappable with Priced[Recipe] with Typed {

    def gi = guild_ingredients.fold("None") { _.map { uc => uc.count + " x #" + uc.upgrade_id } mkString ", " }
    def il = ingredients map { ic => ic.count + " x #" + ic.item_id } mkString ", "

    def toMap = Map(
        "id" -> id.toString,
        "type" -> `type`,
        "output_item_id" -> output_item_id.toString,
        "output_item_count" -> output_item_count.toString,
        "time_to_craft_ms" -> time_to_craft_ms.toString,
        "disciplines" -> noneOrSorted(disciplines),
        "min_rating" -> min_rating.toString,
        "flags" -> noneOrSorted(flags),
        "ingredients" -> il,
        "guild_ingredients" -> gi,
        "output_upgrade_id" -> noneOrString(output_upgrade_id),
        "chat_link" -> chat_link)

    def withPrices(b: Option[Int], s: Option[Int]) =
        copy(buy = b, sell = s)

}

case class ItemCount(
    item_id: Int,
    count: Int)

case class UpgradeCount(
    upgrade_id: Int,
    count: Int)

object RecipeProtocols extends DefaultJsonProtocol {

    implicit val fmtUpgradeCount = jsonFormat2(UpgradeCount)
    implicit val fmtItemCount = jsonFormat2(ItemCount)
    implicit val fmtRecipe = jsonFormat14(Recipe)

}

object Recipes extends PersistentMapCatalog[Int, Recipe] with IntIdCatalog {

    import RecipeProtocols._

    val name = "recipes"
    val url = root + "/recipes"

    def bulkConvert(v: JsValue) = v.convertTo[List[Recipe]]

}
