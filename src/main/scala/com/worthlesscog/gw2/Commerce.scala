package com.worthlesscog.gw2

import spray.json.{DefaultJsonProtocol, JsValue}

case class Price(
    id: Int,
    whitelisted: Boolean,
    buys: Option[TradeInfo],
    sells: Option[TradeInfo]) extends Id[Int]

case class TradeInfo(
    unit_price: Int,
    quantity: Int)

object PriceProtocols extends DefaultJsonProtocol {

    implicit val fmtTradeInfo = jsonFormat2(TradeInfo)
    implicit val fmtPrice = jsonFormat4(Price)

}

object Prices extends MapCatalog[Int, Price] with IntIdCatalog {

    import PriceProtocols._

    val name = "prices"
    val url = root + "/commerce/prices"

    def bulkConvert(v: JsValue) = v.convertTo[List[Price]]

}

object Commerce {

    def prices(ids: Iterable[Int]) = Loader.downloadMap(ids, Prices)

}
