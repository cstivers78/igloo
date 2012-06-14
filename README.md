# igloo

A Scala library for utilizing localized resource bundles.

Igloo provides easy to use interface on top of PropertyResourceBundles, but loads resources using UTF-8 by default.

## Usage

Let's assume you have a properties file named `src/main/resources/i18n/messages_en.properties` with the following contents:

    greeting = Hello
    personal_greeting = Hello {0}

The property files is a like a Java property resource files, except the contents can be UTF-8 encoded.

To load the properties file, you will need to first create an instance of `Igloo`. 

`Igloo()` accepts three parameters:

- `resolve: String=>Option[URI]` - Resolve the location of bundles. Default: `Igloo.resourceResolver("i18n")`, which looks into the `i18n` subdirectory of the resources path.
- `create: (String,Locale,URI)=>Bundle` - Create a new `Bundle`. Default: `Igloo.createBundle`, which loads the resources using a reader and `PropertyResourceBundle`.
- `default: Bundle` - the default Bundle to use if one can not be found. Default: `Bundle.empty`.


The following will instantiate `Igloo` with default arguments:

    import us.stivers.igloo.Igloo

    val igloo = Igloo()


Once you have an instance of `Igloo`, you can then load bundles. To load a bundle, you need to provide the name of the bundle and the locale. The locale is required and there are several methods you can use to specify it:

- `language`, `country` and `variant` parameters. 
- An instance of igloo's `Locale`.
- An instance of Java's `java.util.Locale`.

Some examples:

    val bundle1 = igloo("messages","en")
    val bundle4 = igloo("messages",Locale("en"))
    val bundle4 = igloo("messages",new java.util.Locale("en"))
    val bundle3 = igloo("messages",java.util.Locale.ENGLISH)

Each of the statements above will attempt to load `message_en.properties`. If that bundle is not found, then it will truncate the name and attempt to load `message.properties`.  

Going forward, we will use this:

    val bundle = igloo("messages","en")

With the bundle loaded, you can then access the messages in the bundle:

    bundle("greeting")
    // Hello

    bundle("personal_greeting")
    // Hello {0}

Notice the `personal_greeting` message was not formatted. You can pass arguments to the message for formatting as such:

    bundle("personal_greeting")("Bob")
    // Hello Bob

The Bundles created using `Igloo.createBundle` utilize `java.text.MessageFormat` for formatting messages. You can easily use a different formatting library simply by replacing the `create` parameter to `Igloo()` with one of you own. 

If you happen to be in a situation where you can not provide arguments to the message for formatting, you can instead format the messages by using map. This will generate a new bundle with formatted messages:

    val bundleA = bundle.map {
      case ("personal_greeting",m) => m("Bill")
      case (_,m) => m
    }

The `map` function takes a function argument of: `((String,Message))=>Message`, where `String` is the resource key and message is the message resource. The result of the `map` function is a new bundle containing the updated messages. So, when you get the `personal_greeting` message from this new bundle, it will already be formatted:

    bundleA("personal_greeting")
    // Hello Bill

This method of formatting messages is especially useful when you want to use a "logic-less" templating system such as Mustache, where you are not able to call functions. 



## Notes

Igloo currently silently fails when a bundle or resource is not found. This is designed and expected because, as it is performing operations with the expectation that if a bundle or resource is not found, you want to still continue and get an empty string. 

If this is not sufficient for you, then you have a couple options:
1. Utilize the `get` methods from `Igloo` and `Bundle`, they return `Option`, which you can then use to perform additional handling.
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
