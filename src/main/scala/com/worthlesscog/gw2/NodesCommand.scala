package com.worthlesscog.gw2

import Utils.info

class NodesCommand extends Command {

    val bindings = List("nodes")

    def dump(f: String => String)(s: Set[String]) =
        s.toSeq.sorted foreach {
            f(_) |> info
        }

    def execute(cmd: List[String]): Unit = cmd match {
        case Nil =>
            nodes |> dump(ticked(accountNodes))

        case _ =>
    }

    def ticked(s: Set[String])(v: String) =
        s"  ${ if (s contains v) TICK else " " }  $v\n"

    val uses = Some(Map("nodes" -> "list home instance nodes"))

}
