package com.worthlesscog.gw2

import java.io.InputStream
import java.net.URL
import java.nio.file.{ Files, Path }

import scala.language.postfixOps

import Utils.{ authenticatedUrl, bis, info, ois, saveObject, using, utf8 }
import spray.json.JsonParser

class Loader {

    val user_agent = """Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0"""

    def downloadAuthenticatedBlobs[K, V <: Id[K]](c: BlobCatalog[K, V]) =
        config.token.fold(Map.empty[K, V]) { t =>
            s"  Downloading ${c.name}...\n" |> info
            val l = authenticatedUrl(c.url, t) |> downloadJson map c.bulkConvert
            l.fold(Map.empty[K, V]) { _.map { t => t.id -> t } toMap }
        }

    def downloadAuthenticatedIds[K](c: IdCatalog[K]) = {
        config.token.fold(Set.empty[K]) {
            s"  Downloading ${c.name}...\n" |> info
            authenticatedUrl(c.url, _) |> downloadIds(c)
        }
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
            case t: Throwable =>
                t.getLocalizedMessage + "\n" |> info
                None
        }

    def downloadMap[K, T <: Id[K]](c: MapCatalog[K, T]): Map[K, T] = {
        val l = c.url |> downloadIds(c)
        s"  Downloading ${l.size} ${c.name}...\n" |> info
        downloadMap(l, c)
    }

    def downloadMap[K, T <: Id[K]](ids: Iterable[K], c: MapCatalog[K, T]): Map[K, T] = {
        val i = ids.grouped(c.chunks) flatMap {
            c.bulkUrl(_) |> downloadJson map c.bulkConvert
        } flatten

        i map { t => t.id -> t } toMap
    }

    def loadPersistentMap[K, T <: Id[K]](c: PersistentMapCatalog[K, T])(p: Path) =
        if (Files.exists(p)) {
            s"  Loading ${c.name}...\n" |> info
            using(p |> ois) { c.read }
        } else {
            downloadMap(c) |> saveObject(p)
        }

    def loadStream(s: InputStream): Array[Byte] =
        Stream.continually(s.read) takeWhile { -1 != } map { _.toByte } toArray

}
