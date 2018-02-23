package com.worthlesscog.gw2

import scala.language.postfixOps

import Utils.info

class Console {

    lazy val bindings = commands flatMap { c => c.bindings map { _ -> c } } toMap

    def process(command: String): Boolean =
        command.trim split " " map { _.trim } toList match {
            case "q" :: _ =>
                false

            case "quit" :: _ =>
                false

            case h :: t if (h nonEmpty) =>
                val p = bindings filter { case (k, _) => k startsWith h }
                p.size match {
                    case 0 => s"$h??\n" |> info
                    case 1 => p.values.head execute t
                    case _ => p.keys.mkString("", ", ", "\n") |> info
                }
                true

            case _ =>
                true
        }

    def run() {
        "? for help\n\n" |> info
        do {
            " > " |> info
        } while (process(io.StdIn.readLine))
    }

}
