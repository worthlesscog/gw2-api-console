package com.worthlesscog.gw2

import Utils.{ noneOrSorted, noneOrString }
import spray.json.{ DefaultJsonProtocol, JsValue }

case class Color(
        id: Int,
        name: String,
        base_rgb: List[Int],
        cloth: ColorDetails,
        leather: ColorDetails,
        metal: ColorDetails,
        item: Option[Int],
        categories: Set[String],
        collection: Option[String],
        buy: Option[Int],
        sell: Option[Int]) extends Categorized with Collected[Color] with Id[Int] with Itemized with Mappable with Named with Priced[Color] {

    def inCollection(s: String) = copy(collection = Some(s))

    def toMap = Map(
        "id" -> id.toString,
        "name" -> name,
        "base_rgb" -> base_rgb.mkString(", "),
        "item" -> noneOrString(item),
        "categories" -> noneOrSorted(categories),
        "cloth" -> cloth.toString,
        "leather" -> leather.toString,
        "metal" -> metal.toString,
        "collection" -> noneOrString(collection))

    override def toString = name

    def withPrices(b: Option[Int], s: Option[Int]) =
        copy(buy = b, sell = s)

}

case class ColorDetails(
        brightness: Int,
        contrast: Float,
        hue: Int,
        saturation: Float,
        lightness: Float,
        rgb: List[Int]) {

    def rgbs = rgb.mkString("[", ", ", "]")

    override def toString = s"brightness = $brightness, contrast = $contrast, hue = $hue, saturation = $saturation, lightness = $lightness, rgb = $rgbs"

}

object ColorProtocols extends DefaultJsonProtocol {

    implicit val fmtColorDetails = jsonFormat6(ColorDetails)
    implicit val fmtColor = jsonFormat11(Color)

}

object Colors extends IntIdCatalog with PersistentMapCatalog[Int, Color] {

    import ColorProtocols._

    val name = "colors"
    val url = root + "/colors"

    def bulkConvert(v: JsValue) = v.convertTo[List[Color]]

}
