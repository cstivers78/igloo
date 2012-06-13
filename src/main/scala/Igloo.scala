package us.stivers.igloo

import java.net.URI

case class Igloo(resolve: String=>Option[URI] = Igloo.resolveBundle, create: (String,Locale,URI)=>Bundle = Igloo.createBundle, default: Bundle = Bundle.empty) {

  private[this] def bundleName(name: String, locale: Locale): String = {
    val b = new StringBuilder(name)
    locale.language.foreach(b += '_' ++= _)
    locale.country.foreach(b += '_' ++= _)
    locale.variant.foreach(b += '_' ++= _)
    b ++= ".properties"
    b.toString
  }

  private[this] def resolveURI(name: String, locale: Locale): Option[(String,Locale,URI)] = resolve(bundleName(name,locale)).map((name,locale,_))

  private[this] def resolveBundle(name: String, locale: Locale): Option[(String,Locale,URI)] = {
    locale match {
      case Locale( Some(_), Some(_), Some(_) )  => resolveURI(name, locale).orElse(resolveBundle(name, locale.copy(variant = None)))
      case Locale( Some(_), Some(_), None )     => resolveURI(name, locale).orElse(resolveBundle(name, locale.copy(country = None)))
      case Locale( Some(_), None,    None )     => resolveURI(name, locale).orElse(resolveBundle(name, locale.copy(language = None)))
      case Locale( _, _, _)                     => resolveURI(name, Locale.empty)
    }
  }

  private[this] val tupledCreate = create.tupled

  def get(name: String, locale: Locale): Option[Bundle] = {
    resolveBundle(name, locale).map(tupledCreate)
  }

  def apply(name: String, locale: Locale): Bundle = {
    get(name, locale).getOrElse(default)
  }

  def apply(name: String, language: String): Bundle = {
    apply(name, Locale(language))
  }

  def apply(name: String, language: String, country: String): Bundle = {
    apply(name, Locale(language, country))
  }

  def apply(name: String, language: String, country: String, variant: String): Bundle = {
    apply(name, Locale(language, country, variant))
  }

}

object Igloo {

  val resolveBundle = resourceResolver("i18n/")

  val createBundle = (name: String, locale: Locale, uri: URI) => {
    import java.util.{PropertyResourceBundle}
    import scala.collection.JavaConversions.enumerationAsScalaIterator
    import scala.io.Source
    val bundle = new PropertyResourceBundle(Source.fromURI(uri).bufferedReader)
    val messages = bundle.getKeys.map(key => key -> Message(bundle.getString(key), locale, Seq.empty, Message.format)).toMap
    Bundle(name, locale, uri, messages)
  }

  def fileResolver(basePath: String) = (name: String) => {
    Option(new java.io.File(basePath,name)).filter(f => f.exists && f.isFile && f.canRead).map(_.toURI)
  }

  def resourceResolver(basePath: String) = (name: String) => {
    Option(Thread.currentThread.getContextClassLoader.getResource(new java.io.File(basePath,name).toString)).map(_.toURI)
  }


}