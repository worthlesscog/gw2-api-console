package com.worthlesscog.gw2

import Utils.presentIn

class UnlockedCommand extends LockedOrUnlocked {

    val bindings = List("unlocked")

    def execute(cmd: List[String]): Unit = execute(cmd, presentIn[Int, Skin], None)

    val uses = Some(Map("unlocked [#weight] [#type]" -> "list unlocked skins"))

}
