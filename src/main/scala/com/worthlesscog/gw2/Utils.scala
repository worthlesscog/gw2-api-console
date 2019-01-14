package com.worthlesscog.gw2

import java.io.{BufferedInputStream, FileInputStream, FileOutputStream, InputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.file.Path

import GuildWarsData.dyeSets

import scala.collection.immutable.SortedMap
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

object Utils {

    def absentFrom[K, V](s: Set[K])(m: Map[K, V]) =
        m filter { case (k, _) => !s.contains(k) }

    def asString(t: Any): String = t.toString

    def authenticatedUrl(url: String, token: String) = url + "?access_token=" + token

    def bis(s: InputStream) = new BufferedInputStream(s)

    def bulkIdUrl(url: String, ids: Iterable[_]) = url + "?ids=" + ids.mkString(",")

    def byBuyPrice[T](a: (_, Priced[T]), b: (_, Priced[T])) = Ordering[Option[Int]].lt(a._2.buy, b._2.buy)

    def byName[T <: Named](a: (_, T), b: (_, T)) = a._2.name < b._2.name

    def categorized[K, V <: Categorized](c: String)(m: Map[K, V]) =
        m filter { case (_, r) => r.categories contains c }

    def cmpLeft(a: (String, _), b: (String, _)) = a._1 < b._1

    def cmpRight(a: (_, String), b: (_, String)) = a._2 < b._2

    def collectable[K, V <: Collected[V]](m: Map[K, V]) =
        m filter { case (_, v) => v.collection.nonEmpty }

    def collectionOf(achievements: Map[Int, Achievement], collectionType: String) =
        achievements |> itemSets filter {
            case (_, a) =>
                a.bits.forall { b =>
                    b.head.`type` == collectionType && b.forall { _.`type` == b.head.`type` }
                }
        }

    def detailTypesOf[V <: Detailed](m: Map[_, V]) =
        m map { case (_, v) => v.details.`type` } toSet

    def dump[K, V](s: ((K, V), (K, V)) => Boolean, f: V => String, t: Option[Map[K, V] => Option[String]] = None, limit: Int = 0)(m: Map[K, V]) =
        if (m nonEmpty) {
            val fmt = "  %-" + (m.keys |> longest) + "s  %s\n"
            val sorted = m.toSeq.sortWith(s)
            val vs = if (limit > 0) sorted.take(limit) else sorted
            vs foreach {
                case (k, v) =>
                    fmt.format(k, f(v)) |> info
            }
            t foreach {
                _ (m) foreach { fmt.format("", _) |> info }
            }
        }

    def dumpAndTally[K, V](s: ((K, V), (K, V)) => Boolean, f: V => String, limit: Int = 0)(m: Map[K, V]) = {
        m |> dump(s, f, None, limit)
        val n = if (limit > 0) m.size.min(limit) else m.size
        if (n > 0)
            f"$n row(s)\n" |> info
    }

    def dumpCollections[V <: Collected[V] with Id[Int] with Named](f: V => String, t: Option[Map[Int, V] => Option[String]] = None)(m: Map[String, Iterable[V]]) = {
        val sorted = SortedMap[String, Iterable[V]]() ++ m
        sorted foreach {
            case (c, vs) =>
                s"$c\n" |> info
                val m = (vs map { v => v.id -> v } toMap)
                m |> dump(byName, f, t)
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

    def isPriced[K, V <: Priced[_]](m: Map[K, V]) = m filter { case (_, v) => v.sell.nonEmpty && v.sell.get > 0 }

    def isTradeable[T <: Flagged](t: T) =
        t |> notFlagged(Set("AccountBound", "SoulbindOnAcquire"))

    def itemSets[K](m: Map[K, Achievement]) =
        m filter { case (_, a) => a.`type` == "ItemSet" && a.bits.nonEmpty && a.bits.get.size > 1 }

    def load() = {
        achievementCategories = loadAchievementCategories
        achievementGroups = loadAchievementGroups
        achievements = loadAchievements
        colors = loadColors
        items = loadItems
        itemStats = loadItemStats
        masteries = loadMasteries
        minis = loadMinis
        nodes = loadNodes
        races = loadRaces
        recipes = loadRecipes
        skins = loadSkins
        titles = loadTitles

        update
    }

    def loadAchievementCategories =
        home.resolve(ACHIEVEMENT_CATEGORIES) |> Loader.loadMap(AchievementCategories)

    def loadAchievementGroups =
        home.resolve(ACHIEVEMENT_GROUPS) |> Loader.loadMap(AchievementGroups)

    def loadAchievements =
        home.resolve(ACHIEVEMENTS) |> Loader.loadMap(Achievements)

    def loadColors =
        home.resolve(COLORS) |> Loader.loadMap(Colors)

    def loadItems =
        home.resolve(ITEMS) |> Loader.loadMap(Items)

    def loadItemStats =
        home.resolve(ITEM_STATS) |> Loader.loadMap(ItemStats)

    def loadMasteries =
        home.resolve(MASTERIES) |> Loader.loadMap(Masteries)

    def loadMinis =
        home.resolve(MINIS) |> Loader.loadMap(Minis)

    def loadNodes =
        home.resolve(NODES) |> Loader.loadPersistentSet(Nodes)

    def loadRaces =
        home.resolve(RACES) |> Loader.loadMap(Races)

    def loadRecipes =
        home.resolve(RECIPES) |> Loader.loadMap(Recipes)

    def loadSkins =
        home.resolve(SKINS) |> Loader.loadMap(Skins)

    def loadTitles =
        home.resolve(TITLES) |> Loader.loadMap(Titles)

    def longest(i: Traversable[_]) =
        i map { _.toString length } max

    def mapRecipeItems =
        consumables filter { case (_, c) => c.details.unlock_type.contains("CraftingRecipe") }

    def mapSkinItems = {
        val (ts, other) = (items |> tradeable).values partition {
            case c: Consumable => c.details.`type` == "Transmutation"
            case _             => false
        }
        val plain = other filter { _.default_skin nonEmpty } groupBy { _.default_skin get } map { case (k, it) => k -> it.map { _.id }.toSeq }
        ts.foldLeft(plain) {
            case (m, t: Consumable) =>
                t.details.skins.fold(m) { _.foldLeft(m) { (m, i) => m + (i -> (m.getOrElse(i, Seq.empty[Int]) :+ t.id)) } }
        }
    }

    def matchingName[K, V <: Named](c: String)(m: Map[K, V]) =
        m filter { case (_, i) => i.name.contains(c) }

    def nameOrId[V <: Named](m: Map[Int, V], id: Int) = m.get(id).fold(s"#$id") { _.name }

    def newLine = "\n" |> info

    def noneOrCommas(o: Option[Iterable[_]]) =
        o.fold("None") { _.mkString(", ") }

    def noneOrSorted(s: Set[_]) =
        if (s isEmpty) "None" else (s map { _.toString } toList).sorted mkString ", "

    def noneOrString(o: Option[_]) =
        o.fold("None") { _.toString }

    def noneOrStrings(os: Traversable[Option[_]]) =
        os.map { noneOrString(_) } mkString ", "

    def notFlagged[K, V <: Flagged](fs: Set[String])(m: Map[K, V]): Map[K, V] =
        m filter { case (_, v) => v |> notFlagged(fs) }

    def notFlagged[T <: Flagged](fs: Set[String])(t: T) =
        t.flags intersect fs isEmpty

    def ofDetailType[K, V <: Detailed](t: String)(m: Map[K, V]) =
        m filter { case (_, v) => v.details.`type` == t }

    def ofType[K, V <: Typed](t: String)(m: Map[K, V]) =
        m filter { case (_, v) => v.`type` == t }

    def ofWeight[K](t: String)(m: Map[K, ArmorSkin]) =
        m filter { case (_, s) => s.details.weight_class == t }

    def ois(p: Path) = new ObjectInputStream(p |> fis)

    def oos(p: Path) = new ObjectOutputStream(p |> fos)

    def optLabelledFloat(o: Option[Float], l: String) =
        o.fold(None: Option[String]) { f => Some(l + " " + f) }

    def optPrice(p: Option[Int], label: String) =
        p map { label + toStringPrice(_) }

    def presentIn[K, V](s: Set[K])(m: Map[K, V]) =
        m filter { case (k, _) => s contains k }

    def price[T <: Id[Int] with Priced[T]](m: Map[Int, T], toItem: Map[T, Seq[Int]]) = {
        val ids = toItem.values.flatten.toSet filter { items.get(_).fold(false) { i => i.isUnpriced && isTradeable(i) } }
        if (ids nonEmpty) {
            val prices = ids |> Commerce.prices
            val itemUpdates = prices map { case (i, p) => i -> (items(i) |> updatePrice(p)) }
            items = items ++ itemUpdates
        }
        val updates = toItem.foldLeft(Map.empty[Int, T]) {
            case (m, (t, i)) =>
                val is = i flatMap { items.get } filter { i => i.sell.nonEmpty && i.sell.get > 0 } sortBy { _.sell.get }
                is.headOption.foldLeft(m) { case (m, i) => m + (t.id -> (t |> updatePrice(i.price))) }
        }
        m ++ updates
    }

    def priceByItem[T <: Id[Int] with Itemized with Priced[T]](m: Map[Int, T]) = {
        val toItem = m flatMap { case (_, t) => t.item.map { t -> Seq(_) } }
        price(m, toItem)
    }

    def priceItems(m: Map[Int, Item]) = {
        val toItem = unpriced(m) map {
            case (_, i) => i -> Seq(i.id)
        }
        price(m, toItem)
    }

    def priceMinis(m: Map[Int, Mini]) = {
        val toItem = unpriced(m) flatMap {
            case (_, p) => items.get(p.item_id) map { i => p -> Seq(i.id) }
        }
        price(m, toItem)
    }

    def priceRecipes(m: Map[Int, Recipe]) = {
        val ri = recipeItems
        val toItem = unpriced(m) flatMap {
            case (_, r) => ri.find { case (_, c) => c.details.recipe_id.contains(r.id) }.map { case (i, _) => r -> Seq(i) }
        }
        price(m, toItem)
    }

    def prices[T <: Priced[T]](t: T) = {
        val ps = List(optPrice(t.buy, "B "), optPrice(t.sell, "S ")).flatten
        if (ps isEmpty) "" else ps.mkString(", ", ", ", "")
    }

    def priceSkins[K](m: Map[Int, Skin]) = {
        val toItem = unpriced(m) flatMap {
            case (_, s) => skinToItems.get(s.id) map { s -> _ }
        }
        price(m, toItem)
    }

    def retry[T](n: Int)(f: => T): Either[Throwable, T] =
        Try {
            f
        } match {
            case Success(t) => Right(t)
            case _ if n > 1 =>
                // XXX - simplistic backoff
                Thread.sleep(1000)
                retry(n - 1)(f)
            case Failure(x) => Left(x)
        }

    def saveObject[A](p: Path)(a: A): A = {
        using(p |> oos) { _.writeObject(a) }
        a
    }

    def select[K, T](m: Map[K, _ >: T])(implicit tag: ClassTag[T]) =
        m flatMap {
            case (k, t: T) => Some(k -> t)
            case _         => None
        }

    def sellPrice[T <: Priced[T]](t: T) =
        optPrice(t.sell, "S ").fold("")(", " +)

    def splitAndBar(s: String) = s split "(?=\\p{Upper})" map (_.toLowerCase) mkString "_"

    def ticked[T <: Id[Int] with Named](s: Set[Int])(c: T): String = {
        val state = if (s contains c.id) TICK else " "
        s"$state  ${ c.name }"
    }

    def tickedAndPriced[T <: Id[Int] with Named with Priced[T]](s: Set[Int])(c: T): String =
        if (s contains c.id)
            s"$TICK  ${ c.name }${ sellPrice(c) }"
        else
            s"   ${ c.name }${ prices(c) }"

    def toCollections[T <: Collected[T]](m: Map[_, T]) =
        (m |> collectable).values groupBy { _.collection.get }

    def toItems(m: Map[_, Recipe]) =
        m map {
            case (k, r) =>
                val o = r.output_item_id
                k -> items.get(o).fold(s"Item #$o Missing")(i => s"${ i.name }${ r.sell.fold("") { ", " + toStringPrice(_) } }")
        }

    def toMap[T <: Id[Int]](ts: Iterable[T]) =
        ts map { t => t.id -> t } toMap

    def toStringPrice(i: Int) = {
        val (g, s, c) = (i / 10000, i / 100 % 100, i % 100)
        val gs = if (g > 0) g + "g" else ""
        val ss = if (s > 0) s + "s" else ""
        val cs = if (c > 0) c + "c" else if (g > 0 || s > 0) "" else "0c"
        gs + ss + cs
    }

    def tradeable[K, V <: Flagged](m: Map[K, V]) =
        m |> notFlagged(Set("AccountBound", "SoulbindOnAcquire"))

    def typesOf[V <: Typed](m: Map[_, V]) =
        m.values map { _.`type` } toSet

    def unpriced[K, V <: Priced[V]](m: Map[K, V]) =
        m filter { case (_, v) => v.isUnpriced }

    def update() = {
        config.token foreach { t =>
            accountAchievements = Loader.downloadAuthenticatedBlobs(AccountAchievements, t)
            accountBuys = Loader.downloadAuthenticatedBlobs(AccountBuys, t)
            accountDyes = Loader.downloadAuthenticatedIds(AccountDyes, t)
            accountMinis = Loader.downloadAuthenticatedIds(AccountMinis, t)
            accountNodes = Loader.downloadAuthenticatedIds(AccountNodes, t)
            accountRecipes = Loader.downloadAuthenticatedIds(AccountRecipes, t)
            accountSkins = Loader.downloadAuthenticatedIds(AccountSkins, t)
            accountTitles = Loader.downloadAuthenticatedIds(AccountTitles, t)
        }

        updateAchievementCategories
        updateAchievementGroups
        updateAchievements
        updateColors
        updateItems
        updateItemStats
        updateMasteries
        updateMinis
        // updateNodes
        updateRaces
        updateRecipes
        updateSkins
        updateTitles

        achievementFlags = flagsOf(achievements)
        achievementTypes = typesOf(achievements)
        // armor = select[Int, Armor](items)
        collections = achievements |> itemSets
        colorCategories = colors.values.flatMap { _.categories }.toSet
        consumables = select[Int, Consumable](items)
        disciplines = recipes.values.flatMap { _.disciplines }.toSet
        itemFlags = flagsOf(items)
        itemTypes = typesOf(items)
        raceNames = races.keys.toSet
        recipeFlags = flagsOf(recipes)
        recipeTypes = typesOf(recipes)
        skinFlags = flagsOf(skins)
        skinTypes = typesOf(skins)
        // weapons = select[Int, Weapon](items)

        armorSkins = select[Int, ArmorSkin](skins)
        weaponSkins = select[Int, WeaponSkin](skins)

        armorTypes = detailTypesOf(armorSkins)
        armorWeights = armorSkins.values.map { _.details.weight_class }.toSet
        weaponTypes = detailTypesOf(weaponSkins)
    }

    def updateAchievementCategories = {
        val (u, m) = home.resolve(ACHIEVEMENT_CATEGORIES) |> Loader.updateMap(AchievementCategories, achievementCategories)
        newAchievementCategories = u
        achievementCategories = m
    }

    def updateAchievementGroups = {
        val (u, m) = home.resolve(ACHIEVEMENT_GROUPS) |> Loader.updateMap(AchievementGroups, achievementGroups)
        newAchievementGroups = u
        achievementGroups = m
    }

    def updateAchievements = {
        val (u, m) = home.resolve(ACHIEVEMENTS) |> Loader.updateMap(Achievements, achievements)
        newAchievements = u
        achievements = m
    }

    // XXX - dirty
    def updateCollectionsFromAchievements[V <: Collected[V]](progressType: String)(m: Map[Int, V]) =
        collectionOf(achievements, progressType).foldLeft(m) {
            case (m, (_, a)) =>
                a.bits.fold(m) {
                    _.foldLeft(m) {
                        case (m, MinipetProgress(_, Some(id))) => m.get(id).fold(m) { v => m + (id -> v.inCollection(a.name)) }
                        case (m, SkinProgress(_, Some(id)))    => m.get(id).fold(m) { v => m + (id -> v.inCollection(a.name)) }
                        case _                                 => m
                    }
                }
        }

    def updateCollectionsFromData[K, V <: Collected[V] with Id[K] with Named](collections: Map[String, Set[String]])(m: Map[K, V]) = {
        collections.foldLeft(m) {
            case (m, (s, set)) =>
                set.foldLeft(m) { (m, n) =>
                    m.values.find(_.name == n).fold(m) { c => m + (c.id -> c.inCollection(s)) }
                }
        }
    }

    def updateColors = {
        val (u, m) = home.resolve(COLORS) |> Loader.updateMap(Colors, colors)
        newColors = u
        colors = m |> updateCollectionsFromData(dyeSets)
    }

    def updateItems = {
        val (u, m) = home.resolve(ITEMS) |> Loader.updateMap(Items, items)
        newItems = u
        items = m
    }

    def updateItemStats = {
        val (u, m) = home.resolve(ITEM_STATS) |> Loader.updateMap(ItemStats, itemStats)
        newItemStats = u
        itemStats = m
    }

    def updateMasteries = {
        val (u, m) = home.resolve(MASTERIES) |> Loader.updateMap(Masteries, masteries)
        newMasteries = u
        masteries = m
    }

    def updateMinis = {
        val (u, m) = home.resolve(MINIS) |> Loader.updateMap(Minis, minis)
        newMinis = u
        minis = m |> updateCollectionsFromAchievements("Minipet")
    }

    def updatePrice[T <: Priced[T]](p: Price)(t: T) =
        t.withPrices(Some(p.buys.fold(0)(_.unit_price)), Some(p.sells.fold(0)(_.unit_price)))

    def updatePrice[T <: Priced[T]](p: (Option[Int], Option[Int]))(t: T) =
        p match { case (b, s) => t.withPrices(b, s) }

    def updateRaces = {
        val (u, m) = home.resolve(RACES) |> Loader.updateMap(Races, races)
        newRaces = u
        races = m
    }

    def updateRecipes = {
        val (u, m) = home.resolve(RECIPES) |> Loader.updateMap(Recipes, recipes)
        newRecipes = u
        recipes = m
    }

    def updateSkins = {
        val (u, m) = home.resolve(SKINS) |> Loader.updateMap(Skins, skins)
        newSkins = u
        skins = m |> updateCollectionsFromAchievements("Skin")
    }

    def updateTitles = {
        val (u, m) = home.resolve(TITLES) |> Loader.updateMap(Titles, titles)
        newTitles = u
        titles = m
    }

    def using[A <: {def close() : Unit}, B](closeable: A)(f: A => B): B =
        try { f(closeable) } finally { closeable.close() }

    def utf8(b: Array[Byte]) = new String(b, "UTF-8")

}
