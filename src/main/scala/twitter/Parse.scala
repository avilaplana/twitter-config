package twitter

import scala.util.matching.Regex


sealed trait Line

case class Group(value: String) extends Line

object Group {

  private val groupRegex = new Regex("^\\[(\\w+)\\]$", "group")

  def asGroup(g: String): Option[Group] = groupRegex findFirstMatchIn g map (m => Group(m.group("group")))
}

case class Property[T](key: String, value: T, environment: Option[String]) extends Line

object Property {

  private val builRegex: String => Regex =
    s => new Regex(s"^(\\w+)(<.*?>)? = $s(;.*)?$$", "key", "environment", "value")

  private val stringRegex = builRegex("\"(.+)\"")
  private val numberRegex = builRegex("([0-9]+)")
  private val booleanRegex = builRegex("(true|false|0|1|yes|no)")
  private val arrayRegex = builRegex("(\\w+(,\\w+)+)")
  private val pathRegex = builRegex("(/?.+/[^/]+/?)")

  private def buildProperty[T](l: String)(regEx: Regex)(value: String => T): Option[Property[_]] =
    regEx findFirstMatchIn l map {
      m => Property(m.group("key"), value(m.group("value")), Option(m.group("environment")))
    }

  def asString(l: String): Option[Property[_]] = buildProperty(l)(stringRegex)(s => s)

  def asNumber(l: String): Option[Property[_]] = buildProperty(l)(numberRegex)(s => s.toLong)

  def asBoolean(l: String): Option[Property[_]] = buildProperty(l)(booleanRegex) {
    _ match {
      case b if b == "true" || b == "yes" || b == "1" => true
      case _ => false
    }
  }

  def asArray(l: String): Option[Property[_]] = buildProperty(l)(arrayRegex)(s => s.split(',').toSeq)

  def asPath(l: String): Option[Property[_]] = buildProperty(l)(pathRegex)(s => s)

}

case object Discardable extends Line


