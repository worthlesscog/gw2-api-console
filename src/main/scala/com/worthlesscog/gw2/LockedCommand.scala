package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.absentFrom

class LockedCommand extends LockedOrUnlocked {

    val bindings = List("locked")
    val bound = Set("AccountBound", "SoulbindOnAcquire")

    def detail(s: Skin) = {
        val is = items.values filter { _.default_skin contains s.id }
        val ss = is map { i =>
            if (bound.intersect(i.flags) isEmpty)
                i.id + ", " + i.toString + " <------------- lookup"
            else
                i.id + ", " + i.toString
        }
        ss.toSet.toList.sorted
    }

    def execute(cmd: List[String]): Unit = execute(cmd, absentFrom[Int, Skin], Some(detail))

    val uses = Some(Map("locked [#weight] [#type]" -> "list locked skins"))

}
