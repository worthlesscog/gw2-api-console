package com.worthlesscog.gw2

import Utils.{ asString, dump, info, cmpLeft }

class HelpCommand extends Command {

    val bindings = List("?", "help")

    def execute(cmd: List[String]): Unit = {
        commands.flatMap(_.uses).fold(Map.empty[String, String]) { _ ++ _ } |> dump(cmpLeft, asString)
        "\n" |> info
        config.toMap |> dump(cmpLeft, asString)
    }

    val uses = None

}
