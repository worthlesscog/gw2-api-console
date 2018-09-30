package com.worthlesscog.gw2

import spray.json.{ DefaultJsonProtocol, JsValue }
import java.util.Date

case class AccountBuy(
    id: Int,
    item_id: Int,
    price: Int,
    quantity: Int,
    created: String) extends Id[Int]

object AccountBuyProtocols extends DefaultJsonProtocol {

    implicit val fmtAccountBuy = jsonFormat5(AccountBuy)

}

object AccountBuys extends BlobCatalog[AccountBuy] {

    import AccountBuyProtocols._

    val name = "account buys"
    val url = root + "/commerce/transactions/current/buys"

    def bulkConvert(v: JsValue) = v.convertTo[List[AccountBuy]]

}
