package us.stivers.igloo

import java.net.{URI}
import java.text.{MessageFormat}
import scala.collection.immutable.{Map,MapProxy}
import scala.collection._
import scala.collection.generic._

case class Bundle(name: String, locale: Locale, uri: URI, messages: Map[String,Message], default: Message = Message.empty) {

  def apply(key: String): Message = get(key).getOrElse(default)

  def contains(key: String): Boolean = get(key).isDefined

  def get(key: String): Option[Message] = messages.get(key)

  def foreach(fn: ((String,Message))=>Unit): Unit = messages.foreach(fn)

  def map(fn: ((String,Message))=>Message): Bundle = copy( messages = messages.map(km => km._1 -> fn(km)) )

  def collect(pf: PartialFunction[(String,Message),Message]): Bundle = {
    val pf2 = new PartialFunction[(String,Message),(String,Message)] {
      def apply(km: (String,Message)) = km._1 -> pf(km)
      def isDefinedAt(km: (String,Message)) = pf.isDefinedAt(km)
    }
    copy( messages = messages.collect(pf2) )
  }

}

object Bundle {
  val empty = Bundle("", Locale.empty, URI.create(""), Map.empty)
}
