package com.worthlesscog.gw2

import Utils.load

class UpdateCommand extends Command {

    val bindings = List("update")

    def execute(cmd: List[String]): Unit = cmd match {
        case _ =>
            home.resolve(ACHIEVEMENT_CATEGORIES) |> Loader.downloadPersistentMap(AchievementCategories)
            home.resolve(ACHIEVEMENT_GROUPS) |> Loader.downloadPersistentMap(AchievementGroups)
            home.resolve(ACHIEVEMENTS) |> Loader.downloadPersistentMap(Achievements)
            home.resolve(COLORS) |> Loader.downloadPersistentMap(Colors)
            home.resolve(ITEM_STATS) |> Loader.downloadPersistentMap(ItemStats)
            home.resolve(ITEMS) |> Loader.downloadPersistentMap(Items)
            home.resolve(MASTERIES) |> Loader.downloadPersistentMap(Masteries)
            home.resolve(MINIS) |> Loader.downloadPersistentMap(Minis)
            home.resolve(RECIPES) |> Loader.downloadPersistentMap(Recipes)
            home.resolve(SKINS) |> Loader.downloadPersistentMap(Skins)
            load
    }

    val uses = Some(Map("update" -> "update data"))

}
