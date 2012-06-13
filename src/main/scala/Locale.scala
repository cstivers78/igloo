package us.stivers.igloo

import java.util.{Locale=>JLocale}

case class Locale(language: Option[String], country: Option[String], variant: Option[String]) {
  lazy val toJavaLocale = new JLocale(language.getOrElse(""), country.getOrElse(""), variant.getOrElse(""))
  override lazy val toString = toJavaLocale.toString
}

object Locale {

  private[this] def opt(s: String): Option[String] = Option(s).map(_.trim).filter(!_.isEmpty)

  def apply(language: String = null, country: String = null, variant: String = null): Locale = Locale(opt(language), opt(country), opt(variant))
  
  lazy val empty = Locale(None,None,None)

  implicit def LocaleToJavaLocale(l: Locale): JLocale = l.toJavaLocale
  implicit def JavaLocaleToLocale(l: JLocale): Locale = Locale(opt(l.getLanguage), opt(l.getCountry), opt(l.getVariant))

}
