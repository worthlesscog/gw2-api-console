package com.worthlesscog.gw2

import Utils.update

class UpdateCommand extends Command {

    val bindings = List("update")

    def execute(cmd: List[String]): Unit = cmd match {
        case _ =>
            update
    }

    val uses = Some(Map("update" -> "update data"))

}
