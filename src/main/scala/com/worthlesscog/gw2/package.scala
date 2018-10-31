package com.worthlesscog

import java.nio.file.Paths

import com.typesafe.scalalogging.Logger
import com.worthlesscog.gw2.Utils.{ mapRecipeItems, mapSkinItems }

package object gw2 {

    implicit class Pipe[A](a: A) {
        def |>[B](f: A => B): B = f(a)
    }

    val ACHIEVEMENTS = "gw2-achievements.gw2"
    val ACHIEVEMENT_CATEGORIES = "gw2-categories.gw2"
    val ACHIEVEMENT_GROUPS = "gw2-groups.gw2"
    val COLORS = "gw2-colors.gw2"
    val ITEMS = "gw2-items.gw2"
    val ITEM_STATS = "gw2-item-stats.gw2"
    val MASTERIES = "gw2-masteries.gw2"
    val MINIS = "gw2-minis.gw2"
    val NODES = "gw2-nodes.gw2"
    val RACES = "gw2-races.gw2"
    val RECIPES = "gw2-recipes.gw2"
    val SKINS = "gw2-skins.gw2"
    val TITLES = "gw2-titles.gw2"

    val SETTINGS = ".gw2-api-console.settings"
    val TICK = "*"

    lazy val commands: List[Command] = List(
        new ArmorCommand,
        new AchievementCategoriesCommand,
        new AchievementsCommand("achievements"),
        new CollectionsCommand,
        new DyesCommand,
        new FlagsCommand,
        new HelpCommand,
        new ItemStatsCommand,
        new ItemsCommand,
        new LockedCommand,
        new MasteriesCommand,
        new NodesCommand,
        new PetsCommand,
        new RecipesCommand,
        new SkinsCommand,
        new TitlesCommand,
        new TokenCommand,
        new UnlockedCommand,
        new UpdateCommand,
        new WeaponsCommand)

    lazy val config = new Config(home)
    lazy val home = Paths.get(System.getProperty("user.home"))
    lazy val log = Logger("GW2")
    lazy val root = "https://api.guildwars2.com/v2"

    var accountAchievements = Map.empty[Int, AccountAchievement]
    var accountBuys = Map.empty[Int, AccountBuy]
    var accountDyes = Set.empty[Int]
    var accountMinis = Set.empty[Int]
    var accountNodes = Set.empty[String]
    var accountRecipes = Set.empty[Int]
    var accountSkins = Set.empty[Int]
    var accountTitles = Set.empty[Int]

    var achievementCategories, newAchievementCategories = Map.empty[Int, AchievementCategory]
    var achievementGroups, newAchievementGroups = Map.empty[String, AchievementGroup]
    var achievements, newAchievements = Map.empty[Int, Achievement]
    var colors, newColors = Map.empty[Int, Color]
    var items, newItems = Map.empty[Int, Item]
    var itemStats, newItemStats = Map.empty[Int, ItemStatSet]
    var masteries, newMasteries = Map.empty[Int, Mastery]
    var minis, newMinis = Map.empty[Int, Mini]
    var nodes = Set.empty[String]
    var races, newRaces = Map.empty[String, Race]
    var recipes, newRecipes = Map.empty[Int, Recipe]
    var skins, newSkins = Map.empty[Int, Skin]
    var titles, newTitles = Map.empty[Int, Title]

    var achievementFlags = Set.empty[String]
    var achievementTypes = Set.empty[String]
    // var armor = Map.empty[Int, Armor]
    var armorSkins = Map.empty[Int, ArmorSkin]
    var armorTypes = Set.empty[String]
    var armorWeights = Set.empty[String]
    var collections = Map.empty[Int, Achievement]
    var colorCategories = Set.empty[String]
    var consumables = Map.empty[Int, Consumable]
    var disciplines = Set.empty[String]
    var itemFlags = Set.empty[String]
    var itemTypes = Set.empty[String]
    var raceNames = Set.empty[String]
    var recipeFlags = Set.empty[String]
    var recipeTypes = Set.empty[String]
    var skinFlags = Set.empty[String]
    var skinTypes = Set.empty[String]
    var weaponSkins = Map.empty[Int, WeaponSkin]
    var weaponTypes = Set.empty[String]
    // var weapons = Map.empty[Int, Weapon]

    lazy val recipeItems = mapRecipeItems
    lazy val skinToItems = mapSkinItems

}
