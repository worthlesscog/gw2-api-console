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

trait BlobCatalog[V] extends Catalog {
    def bulkConvert(v: JsValue): Iterable[V]
}

trait IdCatalog[K] extends Catalog {
    def bulkIds(v: JsValue): Set[K]
    def bulkUrl(ids: Iterable[K]) = bulkIdUrl(url, ids)
}

trait IntIdCatalog extends IdCatalog[Int] {
    def bulkIds(v: JsValue) = v.convertTo[Set[Int]]
}

trait StringIdCatalog extends IdCatalog[String] {
    def bulkIds(v: JsValue) = v.convertTo[Set[String]]
}

trait MapCatalog[K, V] extends IdCatalog[K] with BlobCatalog[V]

trait SetCatalog[V] extends IdCatalog[V] with BlobCatalog[V]

trait PersistentCatalog[T] {
    def read(s: ObjectInputStream) = s.readObject.asInstanceOf[T]
}

trait PersistentMapCatalog[K, V] extends PersistentCatalog[Map[K, V]] with MapCatalog[K, V]

trait PersistentSetCatalog[V] extends PersistentCatalog[Set[V]] with SetCatalog[V]
