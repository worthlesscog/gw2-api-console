package com.worthlesscog.gw2

class ItemsCommand extends FlagNameTypeMap[Item]("items") {

    def execute(cmd: List[String]) = execute(cmd, items, itemFlags, itemTypes)

}
