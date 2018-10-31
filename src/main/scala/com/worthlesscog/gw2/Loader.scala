package com.worthlesscog.gw2

import java.io.InputStream
import java.net.URL
import java.nio.file.{ Files, Path }

import Utils.{ authenticatedUrl, bis, info, ois, retry, saveObject, using, utf8 }
import spray.json.JsonParser

object Loader {

    val RETRIES = 3

    val user_agent = """Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0"""

    def download(url: String) =
        using {
            val c = new URL(url).openConnection
            c.setRequestProperty("User-Agent", user_agent)
            c.getInputStream |> bis
        } {
            loadStream
        }

    def downloadAuthenticatedBlobs[K, V <: Id[K]](c: BlobCatalog[V], token: String) = {
        s"  Downloading ${c.name}...\n" |> info
        val l = authenticatedUrl(c.url, token) |> downloadJson map c.bulkConvert
        l.fold(Map.empty[K, V]) { _.map { v => v.id -> v } toMap }
    }

    def downloadAuthenticatedIds[K](c: IdCatalog[K], token: String) = {
        s"  Downloading ${c.name}...\n" |> info
        authenticatedUrl(c.url, token) |> downloadIds(c)
    }

    def downloadIds[K](c: IdCatalog[K])(url: String) =
        (url |> downloadJson).fold(Set.empty[K]) { c.bulkIds }

    def downloadJson(url: String) =
        retry(RETRIES)(download(url)) match {
            case Right(b) =>
                utf8(b) |> Some.apply map { JsonParser(_) }
            case Left(t) =>
                t.getLocalizedMessage + "\n" |> info
                None
        }

    def downloadMap[K, V <: Id[K]](c: MapCatalog[K, V]): Map[K, V] = {
        val l = c.url |> downloadIds(c)
        s"  Downloading ${l.size} ${c.name}...\n" |> info
        downloadMap(l, c)
    }

    def downloadMap[K, V <: Id[K]](ids: Iterable[K], c: MapCatalog[K, V]): Map[K, V] = {
        val i = ids.grouped(c.chunks) flatMap {
            c.bulkUrl(_) |> downloadJson map c.bulkConvert
        } flatten

        i map { v => v.id -> v } toMap
    }

    def downloadPersistentSet[V](c: PersistentSetCatalog[V])(p: Path) =
        downloadSet(c) |> saveObject(p)

    def downloadSet[V](c: SetCatalog[V]): Set[V] = {
        s"  Downloading ${c.name}...\n" |> info
        c.url |> downloadIds(c)
    }

    def downloadUpdates[K, V <: Id[K]](c: MapCatalog[K, V], m: Map[K, V]): Map[K, V] = {
        s"  Updating ${c.name}...\n" |> info
        val l = c.url |> downloadIds(c)
        val u = l -- m.keySet
        if (u nonEmpty) {
            val e = if (u.size > 10) "..." else ""
            val trunc = u take 10 mkString ","
            s"    New - $trunc$e\n" |> info
            downloadMap(u, c)
        } else Map.empty[K, V]
    }

    def loadMap[K, V <: Id[K]](c: PersistentMapCatalog[K, V])(p: Path) =
        if (Files.exists(p)) {
            s"  Loading ${c.name}...\n" |> info
            using(p |> ois) { c.read }
        } else Map.empty[K, V]

    def loadPersistentSet[V](c: PersistentSetCatalog[V])(p: Path) =
        if (Files.exists(p)) {
            s"  Loading ${c.name}...\n" |> info
            using(p |> ois) { c.read }
        } else
            downloadPersistentSet(c)(p)

    def loadStream(s: InputStream): Array[Byte] =
        Stream.continually(s.read) takeWhile { -1 != } map { _.toByte } toArray

    def updateMap[K, V <: Id[K]](c: PersistentMapCatalog[K, V], m: Map[K, V])(p: Path) = {
        val u = downloadUpdates(c, m)
        (u, (m ++ u) |> saveObject(p))
    }

}
