package com.worthlesscog.gw2

import java.io.ObjectInputStream

import Utils.bulkIdUrl
import spray.json.DefaultJsonProtocol._
import spray.json.JsValue

trait Catalog {
    val chunks = 100
    def name: String
    def url: String
}

trait BlobCatalog[K, V] extends Catalog {
    def bulkConvert(v: JsValue): List[V]
}

trait IdCatalog[K] extends Catalog {
    def bulkIds(v: JsValue): Set[K]
}

trait IntIdCatalog extends IdCatalog[Int] {
    def bulkIds(v: JsValue) = v.convertTo[Set[Int]]
}

trait MapCatalog[K, V] extends BlobCatalog[K, V] with IdCatalog[K] {
    def bulkUrl(ids: Iterable[K]) = bulkIdUrl(url, ids)
}

trait PersistentCatalog[K, V] extends Catalog {
    def read(s: ObjectInputStream) = s.readObject.asInstanceOf[Map[K, V]]
}

trait PersistentMapCatalog[K, V] extends MapCatalog[K, V] with PersistentCatalog[K, V]

trait StringIdCatalog extends IdCatalog[String] {
    def bulkIds(v: JsValue) = v.convertTo[Set[String]]
}
