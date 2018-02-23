package com.worthlesscog.gw2

class UpdateCommand extends Command {

    val bindings = List("update")

    def execute(cmd: List[String]): Unit = cmd match {
        case _ =>
        // XXX - refactor to support
    }

    val uses = Some(Map("update" -> "update data"))

}
