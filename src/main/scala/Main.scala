
import us.stivers.igloo._
import java.util.{Date,Locale}

object Main extends App {

  val bundles = Igloo(Igloo.fileResolver("src/test/resources/i18n"))
  
  val bundle1 = bundles("messages","ko")

  println()
  println(bundle1)
  println()
  println(bundle1("today"))
  println(bundle1("greeting"))
  println()
  println(bundle1("today")(new Date))
  println(bundle1("greeting")("bob"))
  println()
  
  val bundle2 = bundle1.map {
    case ("greeting",m) => m("bill")
    case (_,m) => m
  }

  println()
  println(bundle2)
  println()
  println(bundle2("today"))
  println(bundle2("greeting"))
  println()
  println(bundle2("greeting")("bob"))
  println()
  
  val bundle3 = bundle2.map {
    case ("today",m) => m(new Date)
    case (k,m) => m
  }

  println()
  println(bundle3)
  println()
  println(bundle3("today"))
  println(bundle3("greeting"))
  println()
  println(bundle3("today")(new Date))
  println(bundle3("greeting")("bob"))
  println()
  
}

