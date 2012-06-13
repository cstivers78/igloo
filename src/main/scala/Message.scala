package us.stivers.igloo

import java.text.{MessageFormat}

case class Message(message: String, locale: Locale, arguments: Seq[Any] = Seq.empty, format: (String,Locale,Seq[Any])=>String) {

  def apply(): String = toString

  def apply(arguments: Any*): Message = copy(arguments = arguments)

  override lazy val toString = format(message, locale, arguments)
}

object Message {

  lazy val empty = Message("",Locale.empty,Seq.empty,Message.format)

  def format(message: String, locale: Locale, arguments: Seq[Any]): String = {
    if ( arguments.size > 0 ) {
      (new MessageFormat(message,locale.toJavaLocale)).format(arguments.map(_.asInstanceOf[Object]).toArray, new StringBuffer(), null).toString()
    } else {
      message
    }
  }

}
