package com.worthlesscog.gw2

import Utils.{ asString, cmpLeft, dump }

class TokenCommand extends Command {

    val bindings = List("token")

    def execute(cmd: List[String]): Unit = cmd match {
        case token :: Nil =>
            config.token = token
            config.toMap |> dump(cmpLeft, asString)

        case _ =>
    }

    val uses = Some(Map("token #token" -> "set account access token"))

}
