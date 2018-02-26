package com.worthlesscog

import java.nio.file.Paths

import scala.language.postfixOps

import com.typesafe.scalalogging.Logger

package object gw2 {

    implicit class Pipe[A](a: A) {
        def |>[B](f: A => B): B = f(a)
    }

    val ACHIEVEMENTS = "gw2-achievements.dat"
    val ACHIEVEMENT_CATEGORIES = "gw2-categories.dat"
    val ACHIEVEMENT_GROUPS = "gw2-groups.dat"
    val COLORS = "gw2-colors.dat"
    val ITEMS = "gw2-items.dat"
    val ITEM_STATS = "gw2-item-stats.dat"
    val MASTERIES = "gw2-masteries.dat"
    val MINIS = "gw2-minis.dat"
    val RECIPES = "gw2-recipes.dat"
    val SKINS = "gw2-skins.dat"

    val SETTINGS = ".gw2-api-console.settings"
    val TICK = "*"

    lazy val commands: List[Command] = List(
        new ArmorCommand,
        new AchievementCategoriesCommand,
        new AchievementsCommand,
        new DyesCommand,
        new FlagsCommand,
        new HelpCommand,
        new ItemStatsCommand,
        new ItemsCommand,
        new LockedCommand,
        new MasteriesCommand,
        new PetsCommand,
        new RecipesCommand,
        new SkinsCommand,
        new TokenCommand,
        new UnlockedCommand,
        new UpdateCommand,
        new WeaponsCommand)

    lazy val config = new Config(home)
    lazy val home = Paths.get(System.getProperty("user.home"))
    lazy val loader = new Loader
    lazy val log = Logger("GW2")
    lazy val root = "https://api.guildwars2.com/v2"

    var accountAchievements = Map.empty[Int, AccountAchievement]
    var accountDyes = Set.empty[Int]
    var accountMinis = Set.empty[Int]
    var accountRecipes = Set.empty[Int]
    var accountSkins = Set.empty[Int]

    var achievementCategories = Map.empty[Int, AchievementCategory]
    var achievementGroups = Map.empty[String, AchievementGroup]
    var achievements = Map.empty[Int, Achievement]
    var colors = Map.empty[Int, Color]
    var items = Map.empty[Int, Item]
    var itemStats = Map.empty[Int, ItemStatSet]
    var masteries = Map.empty[Int, Mastery]
    var minis = Map.empty[Int, Mini]
    var recipes = Map.empty[Int, Recipe]
    var skins = Map.empty[Int, Skin]

    var achievementFlags = Set.empty[String]
    var achievementTypes = Set.empty[String]
    var armorSkins = Map.empty[Int, ArmorSkin]
    var armorTypes = Set.empty[String]
    var armorWeights = Set.empty[String]
    var colorCategories = Set.empty[String]
    var disciplines = Set.empty[String]
    var itemFlags = Set.empty[String]
    var itemTypes = Set.empty[String]
    var recipeFlags = Set.empty[String]
    var recipeTypes = Set.empty[String]
    var skinFlags = Set.empty[String]
    var skinTypes = Set.empty[String]
    var weaponSkins = Map.empty[Int, WeaponSkin]
    var weaponTypes = Set.empty[String]

}
