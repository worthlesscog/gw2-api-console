package com.worthlesscog.gw2

import Utils.{ asString, byName, dump, dumpAndTally, flagged, isNumeric, matchingName, ofType, cmpLeft }

abstract class FlagNameTypeMap[T <: FlagNameTypeAndMap](label: String) extends Command {

    val bindings = List(label)

    def dumpObject(t: T) = t.toMap |> dump(cmpLeft, asString)

    def execute(cmd: List[String], m: Map[Int, T], flags: Set[String], types: Set[String]) = cmd match {
        case icft :: Nil =>
            if (icft |> isNumeric)
                m.get(icft.toInt) foreach dumpObject
            else if (flags contains icft)
                m |> flagged(icft) |> dumpAndTally(byName, asString)
            else if (types contains icft)
                m |> ofType(icft) |> dumpAndTally(byName, asString)
            else
                m |> matchingName(icft) |> dumpAndTally(byName, asString)

        case Nil =>
            m |> dumpAndTally(byName, asString)

        case _ =>
    }

    def uses = Some(Map(s"$label [#id | #contains | #flag | #type]" -> s"list $label"))

}
