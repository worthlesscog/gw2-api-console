package com.worthlesscog.gw2

import Utils.{asString, cmpLeft, dump, info}

class HelpCommand extends Command {

    val bindings = List("?", "help")

    def execute(cmd: List[String]): Unit = {
        commands.flatMap(_.uses).fold(Map.empty[String, String]) { _ ++ _ } |> dump(cmpLeft, asString)
        "\n" |> info
        config.toMap |> dump(cmpLeft, asString)
    }

    val uses = None

}
