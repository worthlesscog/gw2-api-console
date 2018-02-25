package com.worthlesscog.gw2

import Utils.load

class UpdateCommand extends Command {

    val bindings = List("update")

    def execute(cmd: List[String]): Unit = cmd match {
        case _ =>
            home.resolve(ACHIEVEMENT_CATEGORIES) |> loader.downloadPersistentMap(AchievementCategories)
            home.resolve(ACHIEVEMENT_GROUPS) |> loader.downloadPersistentMap(AchievementGroups)
            home.resolve(ACHIEVEMENTS) |> loader.downloadPersistentMap(Achievements)
            home.resolve(COLORS) |> loader.downloadPersistentMap(Colors)
            home.resolve(ITEM_STATS) |> loader.downloadPersistentMap(ItemStats)
            home.resolve(ITEMS) |> loader.downloadPersistentMap(Items)
            home.resolve(MASTERIES) |> loader.downloadPersistentMap(Masteries)
            home.resolve(MINIS) |> loader.downloadPersistentMap(Minis)
            home.resolve(RECIPES) |> loader.downloadPersistentMap(Recipes)
            home.resolve(SKINS) |> loader.downloadPersistentMap(Skins)
            load
    }

    val uses = Some(Map("update" -> "update data"))

}
