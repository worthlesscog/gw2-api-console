package com.worthlesscog.gw2

trait Categorized {
    def categories: Set[String]
}

trait Collected[T] {
    def collection: Option[String]
    def inCollection(s: String): T
}

trait Command {
    def bindings: List[String]
    def execute(args: List[String]): Unit
    def uses: Option[Map[String, String]]
}

trait Details extends Typed

trait Detailed {
    def details: Details
}

trait FlagNameTypeAndMap extends Flagged with Mappable with Named with Typed

trait Flagged {
    def flags: Set[String]
}

trait Id[T] {
    def id: T
}

trait Itemized {
    def item: Option[Int]
}

trait Mappable {
    def toMap: Map[String, String]
}

trait Named {
    def name: String
}

trait Priced[T] {
    def buy: Option[Int]
    def sell: Option[Int]
    def withPrices(buy: Option[Int], sell: Option[Int]): T
}

trait Typed {
    def `type`: String
}
