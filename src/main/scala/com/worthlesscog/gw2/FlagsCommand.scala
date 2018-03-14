package com.worthlesscog.gw2

import Utils.dumpTypes

class FlagsCommand extends Command {

    val bindings = List("flags")

    def execute(cmd: List[String]): Unit = cmd match {
        case Nil =>
            // dumpTypes("Achievement Progress Types", achievementProgressTypes.toList)
            // dumpTypes("Achievement Reward Types", achievementRewardTypes.toList)
            dumpTypes("Achievement Flags", achievementFlags.toList)
            dumpTypes("Achievement Types", achievementTypes.toList)
            dumpTypes("Armor Types", armorTypes.toList)
            dumpTypes("Armor Weights", armorWeights.toList)
            dumpTypes("Disciplines", disciplines.toList)
            dumpTypes("Dye Categories", colorCategories.toList)
            // dumpTypes("Item Rarities", itemRarities.toList)
            dumpTypes("Item Flags", itemFlags.toList)
            dumpTypes("Item Types", itemTypes.toList)
            dumpTypes("Races", raceNames.toList)
            dumpTypes("Recipe Flags", recipeFlags.toList)
            dumpTypes("Recipe Types", recipeTypes.toList)
            dumpTypes("Skin Flags", skinFlags.toList)
            dumpTypes("Skin Types", skinTypes.toList)
            dumpTypes("Weapon Types", weaponTypes.toList)

        case _ =>
    }

    val uses = Some(Map("flags" -> "list flags and types"))

}
