# igloo

A Scala library for utilizing localized resource bundles.

Igloo provides easy to use interface on top of PropertyResourceBundles, but loads resources using UTF-8 by default.

## Usage

Let's assume you have a properties file named `resources/messages_en.properties` containing:

    greeting = Hello
    personal_greeting = Hello {0}

To load the properties file, you will need to first get an instance of `Igloo`. 

`Igloo()` accepts two parameters:
  - `resolve: String=>Option[URI]` - Resolve the location of bundles. Default: `Igloo.resourceResolver("i18n")`, which looks into the `i18n` subdirectory of you resources path.
  - `create: (String,Locale,URI)=>Bundle` - Create a new `Bundle`. Default: `Igloo.createBundle`, which loads the resources using a reader and `PropertyResourceBundle`.

The following is instantiating `Igloo` with default arguments:

    import us.stivers.igloo.Igloo

    val igloo = Igloo()


Once you have an instance of `Igloo`, you can then load bundles. To load a bundle, you need to provide the name of the bundle and the locale. The locale is required and you have several methods you can use to specify it:
- `language`, `country` and `variant` parameters. 
- An instance of igloo's `Locale`.
- An instance of Java's `java.util.Locale`.

Some examples:

    val bundle1 = igloo("messages","en")
    val bundle4 = igloo("messages",Locale("en"))
    val bundle4 = igloo("messages",new java.util.Locale("en"))
    val bundle3 = igloo("messages",java.util.Locale.ENGLISH)

Each of the statements above will attempt to load `message_en.properties`. If that bundle is not found, then it will truncate the name and attempt to load `message.properties`. The property files simply property resource files, except the contents can be UTF-8 encoded. This is useful if you are a small team managing translations yourself.


With the bundle loaded, you can then access the messages in the bundle:

    bundle1("greeting")
    // Hello

    bundle1("personal_greeting")
    // Hello {0}

Notice the `personal_greeting` message was not formatted. You can pass arguments to the message for formatting as such:

    bundle1("personal_greeting")("Bob")
    // Hello Bob

The Bundles created using `Igloo.createBundle` utilize `java.text.MessageFormat` for formatting messages. You can easily use a different formatting library simply by replacing the `create` parameter to `Igloo()` with one of you own. 

If you happen to be in a situation where you can not provide arguments to the message for formatting, you can instead format the messages by using map. This will generate a new bundle with formatted messages:

    val bundle5 = bundle1.map {
      case ("personal_greeting",m) => m("Bill")
      case (_,m) => m
    }

The `map` function takes a function argument of: `((String,Message))=>Message`. It will return a new bundle, with the updated messages. So, when you get the `personal_greeting` message, it will already be formatted:

    bundle5("personal_greeting")
    // Hello Bill

This is especially useful when you want to use a "logic-less" templating system such as Mustache. 



## Notes

Igloo currently silently fails. It is performing operations with the expectation that if a bundle or resource is not found, you want to still continue and get an empty string. Well, that is how we use it. 

If this is not sufficient for you, then you have several options:
1. Utilize the `get` methods from `Igloo` and `Bundle`, they return `Option`, which you can then perform additional handling on.
2. Supply alternate values for the `default` constructor argument for `Igloo` and `Bundle`. Currently they are `Bundle.empty` and `Message.empty` (respectfully).

## Dependencies

There are no library dependencies outside of the Scala 2.9.1 standard library and Java 1.5 standard library.

## Building

This project uses SBT. 

To build:

    $ sbt
    > compile

To package:

    $ sbt
    > package

To publish artifacts:

    $ sbt
    > publish
