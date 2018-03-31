package com.worthlesscog.gw2

import java.io.{ FileNotFoundException, InputStream }
import java.net.URL
import java.nio.file.{ Files, Path }

import scala.language.postfixOps

import Utils.{ authenticatedUrl, bis, info, ois, saveObject, using, utf8 }
import spray.json.JsonParser

class Loader {

    val user_agent = """Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0"""

    def downloadAuthenticatedBlobs[K, V <: Id[K]](c: BlobCatalog[K, V], token: String) = {
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
        try {
            val b = using {
                val c = new URL(url).openConnection
                c.setRequestProperty("User-Agent", user_agent)
                c.getInputStream |> bis
            } {
                loadStream
            }
            utf8(b) |> Some.apply map { JsonParser(_) }
        } catch {
            case x: FileNotFoundException =>
                // s"Resource not found ${x.getMessage}\n" |> info
                None
            case t: Throwable =>
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

    def downloadPersistentMap[K, V <: Id[K]](c: PersistentMapCatalog[K, V])(p: Path) =
        downloadMap(c) |> saveObject(p)

    def loadPersistentMap[K, V <: Id[K]](c: PersistentMapCatalog[K, V])(p: Path) =
        if (Files.exists(p)) {
            s"  Loading ${c.name}...\n" |> info
            using(p |> ois) { c.read }
        } else
            downloadPersistentMap(c)(p)

    def loadStream(s: InputStream): Array[Byte] =
        Stream.continually(s.read) takeWhile { -1 != } map { _.toByte } toArray

}
