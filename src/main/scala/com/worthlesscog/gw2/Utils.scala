package com.worthlesscog.gw2

import java.io.{ BufferedInputStream, FileInputStream, FileOutputStream, InputStream, ObjectInputStream, ObjectOutputStream }
import java.nio.file.Path

import scala.collection.immutable.SortedMap
import scala.language.{ postfixOps, reflectiveCalls }
import scala.reflect.ClassTag

import GuildWarsData.dyeSets

object Utils {

    def absentFrom[K, V](s: Set[K])(m: Map[K, V]) =
        m filter { case (k, _) => !s.contains(k) }

    def asString(t: Any): String = t.toString

    def authenticatedUrl(url: String, token: String) = url + "?access_token=" + token

    def bis(s: InputStream) = new BufferedInputStream(s)

    def bulkIdUrl(url: String, ids: Iterable[_]) = url + "?ids=" + ids.mkString(",")

    def byName[T <: Named](a: (_, T), b: (_, T)) = a._2.name < b._2.name

    def categorized[K, V <: Categorized](c: String)(m: Map[K, V]) =
        m filter { case (_, r) => r.categories contains c }

    def cmpLeft(a: (String, _), b: (String, _)) = a._1 < b._1

    def cmpRight(a: (_, String), b: (_, String)) = a._2 < b._2

    def collectable[K, V <: Collected[V]](m: Map[K, V]) =
        m filter { case (_, v) => v.collection.nonEmpty }

    def collectionOf(achievements: Map[Int, Achievement], collectionType: String) =
        achievements.values filter { a =>
            a.`type` == "ItemSet" && a.bits.nonEmpty && a.bits.forall { b =>
                b.size > 1 && b.head.`type` == collectionType && b.forall { _.`type` == b.head.`type` }
            }
        }

    def detailTypesOf[V <: Detailed](m: Map[_, V]) =
        m map { case (_, v) => v.details.`type` } toSet

    def dump[K, V](s: ((K, V), (K, V)) => Boolean, f: V => String)(m: Map[K, V]) =
        if (m nonEmpty) {
            val fmt = "  %-" + (m.keys |> longest) + "s  %s\n"
            m.toSeq.sortWith(s) foreach {
                case (k, v) =>
                    fmt.format(k, f(v)) |> info
            }
        }

    def dumpAndTally[K, V](s: ((K, V), (K, V)) => Boolean, f: V => String)(m: Map[K, V]) = {
        m |> dump(s, f)
        f"${m.size}%d row(s)\n" |> info
    }

    def dumpCollections[V <: Collected[V] with Id[Int] with Named](f: V => String)(m: Map[String, Iterable[V]]) = {
        val sorted = SortedMap[String, Iterable[V]]() ++ m
        sorted foreach {
            case (c, vs) =>
                s"$c\n" |> info
                (vs map { v => v.id -> v } toMap) |> dump(byName, f)
        }
    }

    def dumpTypes(d: String, vs: List[_]) = {
        s"  $d\n" |> info
        vs.grouped(10) foreach {
            _.mkString("    ", ", ", "\n") |> info
        }
    }

    def fis(p: Path) = new FileInputStream(p.toFile)

    def flagged[K, V <: Flagged](f: String)(m: Map[K, V]) =
        m filter { case (_, r) => r.flags contains f }

    def flagsOf[V <: Flagged](m: Map[_, V]) =
        m.values flatMap { _.flags } toSet

    def fos(p: Path) = new FileOutputStream(p.toFile)

    def info(s: String) = log.info(s)

    def isNumeric(s: String) = s forall { _.isDigit }

    def load() = {
        config.token foreach { t =>
            accountAchievements = loader.downloadAuthenticatedBlobs(AccountAchievements, t)
            accountDyes = loader.downloadAuthenticatedIds(AccountDyes, t)
            accountMinis = loader.downloadAuthenticatedIds(AccountMinis, t)
            accountRecipes = loader.downloadAuthenticatedIds(AccountRecipes, t)
            accountSkins = loader.downloadAuthenticatedIds(AccountSkins, t)
            accountTitles = loader.downloadAuthenticatedIds(AccountTitles, t)
        }

        achievementCategories = home.resolve(ACHIEVEMENT_CATEGORIES) |> loader.loadPersistentMap(AchievementCategories)
        achievementGroups = home.resolve(ACHIEVEMENT_GROUPS) |> loader.loadPersistentMap(AchievementGroups)
        achievements = home.resolve(ACHIEVEMENTS) |> loader.loadPersistentMap(Achievements)
        colors = home.resolve(COLORS) |> loader.loadPersistentMap(Colors) |> updateCollectionsFromData(dyeSets)
        itemStats = home.resolve(ITEM_STATS) |> loader.loadPersistentMap(ItemStats)
        items = home.resolve(ITEMS) |> loader.loadPersistentMap(Items)
        masteries = home.resolve(MASTERIES) |> loader.loadPersistentMap(Masteries)
        minis = home.resolve(MINIS) |> loader.loadPersistentMap(Minis) |> updateCollectionsFromAchievements("Minipet")
        recipes = home.resolve(RECIPES) |> loader.loadPersistentMap(Recipes)
        skins = home.resolve(SKINS) |> loader.loadPersistentMap(Skins) |> updateCollectionsFromAchievements("Skin")
        titles = home.resolve(TITLES) |> loader.loadPersistentMap(Titles)

        achievementFlags = flagsOf(achievements)
        achievementTypes = typesOf(achievements)
        colorCategories = colors.values.flatMap { _.categories }.toSet
        disciplines = recipes.values.flatMap { _.disciplines }.toSet
        itemFlags = flagsOf(items)
        itemTypes = typesOf(items)
        recipeFlags = flagsOf(recipes)
        recipeTypes = typesOf(recipes)
        skinFlags = flagsOf(skins)
        skinTypes = typesOf(skins)

        armorSkins = select[Int, ArmorSkin](skins)
        weaponSkins = select[Int, WeaponSkin](skins)

        armorTypes = detailTypesOf(armorSkins)
        armorWeights = armorSkins.values.map { _.details.weight_class }.toSet
        weaponTypes = detailTypesOf(weaponSkins)
    }

    def longest(i: Traversable[_]) =
        i map { _.toString length } max

    def matchingName[K, V <: Named](c: String)(m: Map[K, V]) =
        m filter { case (_, i) => i.name.contains(c) }

    def noneOrCommas(o: Option[Iterable[_]]) =
        o.fold("None") { _.mkString(", ") }

    def noneOrSorted(s: Set[_]) =
        if (s isEmpty) "None" else (s map { _.toString } toList).sorted mkString ", "

    def noneOrString(o: Option[_]) =
        o.fold("None") { _.toString }

    def noneOrStrings(os: Traversable[Option[_]]) =
        os.map { noneOrString(_) } mkString ", "

    def notFlagged[K, V <: Flagged](f: String)(m: Map[K, V]) =
        m filter { case (_, r) => !(r.flags contains f) }

    def ofDetailType[K, V <: Detailed](t: String)(m: Map[K, V]) =
        m filter { case (_, r) => r.details.`type` == t }

    def ofType[K, V <: Typed](t: String)(m: Map[K, V]) =
        m filter { case (_, r) => r.`type` == t }

    def ofWeight[K](t: String)(m: Map[K, ArmorSkin]) =
        m filter { case (_, r) => r.details.weight_class == t }

    def ois(p: Path) = new ObjectInputStream(p |> fis)

    def oos(p: Path) = new ObjectOutputStream(p |> fos)

    def optLabelledFloat(o: Option[Float], l: String) =
        o.fold(None: Option[String]) { f => Some(l + " " + f) }

    def optSellPrice[T <: Priced[T]](t: T) =
        t.sell.fold("")(", " + toStringPrice(_))

    def presentIn[K, V](s: Set[K])(m: Map[K, V]) =
        m filter { case (k, _) => s contains k }

    def reprice[T <: Id[Int] with Itemized with Priced[T]](m: Map[Int, T]) = {
        val byItem = m flatMap { case (_, c) => c.item.map { _ -> c } }
        val prices = byItem.keys |> Commerce.prices
        prices.foldLeft(m) { (m, ip) =>
            ip match {
                case (i, p) =>
                    val nc = byItem(i)
                    m + (nc.id -> (nc |> updatePrice(p)))
            }
        }
    }

    def saveObject[A](p: Path)(a: A): A = {
        using(p |> oos) { _.writeObject(a) }
        a
    }

    def select[K, T](m: Map[K, _ >: T])(implicit tag: ClassTag[T]) = m flatMap {
        case (k, t: T) => Some(k -> t)
        case _         => None
    }

    def splitAndBar(s: String) = s split ("(?=\\p{Upper})") map (_.toLowerCase) mkString "_"

    def tickedAndPriced[T <: Id[Int] with Named with Priced[T]](s: Set[Int])(c: T): String = {
        val (state, price) = if (s contains c.id) (TICK, "") else (" ", optSellPrice(c))
        s"$state  ${c.name}$price"
    }

    def ticked[T <: Id[Int] with Named](s: Set[Int])(c: T): String = {
        val state = if (s contains c.id) TICK else " "
        s"$state  ${c.name}"
    }

    def toCollections[T <: Collected[T]](m: Map[_, T]) =
        m.values filter { _.collection.nonEmpty } groupBy { _.collection.get }

    def toStringPrice(i: Int) = {
        val (g, s, c) = (i / 10000, i / 100 % 100, i % 100)
        val gs = if (g > 0) g + "g" else ""
        val ss = if (s > 0) s + "s" else ""
        val cs = if (c > 0) c + "c" else if (g > 0 || s > 0) "" else "0c"
        gs + ss + cs
    }

    def typesOf[V <: Typed](m: Map[_, V]) =
        m.values map { _.`type` } toSet

    // XXX - dirty
    def updateCollectionsFromAchievements[V <: Collected[V]](progressType: String)(m: Map[Int, V]) =
        collectionOf(achievements, progressType).foldLeft(m) { (m, a) =>
            a.bits.fold(m) {
                _.foldLeft(m) {
                    case (m, MinipetProgress(_, Some(id))) => m + (id -> m(id).inCollection(a.name))
                    case (m, SkinProgress(_, Some(id)))    => m + (id -> m(id).inCollection(a.name))
                    case _                                 => m
                }
            }
        }

    def updateCollectionsFromData[K, V <: Collected[V] with Id[K] with Named](collections: Map[String, Set[String]])(m: Map[K, V]) = {
        collections.foldLeft(m) { (m, s) =>
            s._2.foldLeft(m) { (m, n) =>
                m.values.find(_.name == n).fold(m) { c => m + (c.id -> c.inCollection(s._1)) }
            }
        }
    }

    def updatePrice[T <: Priced[T]](p: Price)(c: T) =
        c.withPrices(Some(p.buys.fold(0)(_.unit_price)), Some(p.sells.fold(0)(_.unit_price)))

    def using[A <: { def close(): Unit }, B](closeable: A)(f: A => B): B =
        try { f(closeable) } finally { closeable.close() }

    def utf8(b: Array[Byte]) = new String(b, "UTF-8")

}
