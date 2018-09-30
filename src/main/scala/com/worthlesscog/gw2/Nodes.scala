package com.worthlesscog.gw2

import spray.json.DefaultJsonProtocol._
import spray.json.JsValue

object Nodes extends PersistentSetCatalog[String] with StringIdCatalog {

    val name = "nodes"
    val url = root + "/nodes"

    def bulkConvert(v: JsValue) = v.convertTo[Set[String]]

}
