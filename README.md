# Exercise - Configuration

Technologies:
=============
```
1. JDK 1.8
2. Scala 2.11.8
3. Sbt 0.13.8
4. Scalatest 3.0.0
```

Run the test
============
```
sbt test
```

Notes
=====

For me the resolution of the exercise can be splitted in 4 parts:

**Parsing of the configuration file into a domain**

I understand the file like a set of lines and I classify each line as one of the following:
 
 ```
 Group
 Property[T]
 Discardable
 ```
 
**Group properties of the domain**

Once the file is defined as a Seq[Line] my target was to  organized the lines by Group using the following structure:
 ```
   private type GroupOfProperties = Map[Group, Seq[Property[_]]]

 ```
 
**Filtering the group of properties by environments**

The idea behind of this part is to remove the properties based on the environments defined as a parameter   

**Simplifying the structure**

To improve the performance and the accesibility I transform from 
```Map[Group, Seq[Property[_]]]``` to ```Map[String, Map[String, Any]]```

**Accessing to the configuration through the Configuration object**

The problem statement is oriented to **dynamically-typed language** like python. I have been trying to get close to that approach but I have to say that 
I have not been 100% because scala **staticlly-typed language**

I have used the trait **Dynamic** that permits:

```
 *  {{{
 *  foo.method("blah")      ~~> foo.applyDynamic("method")("blah")
 *  foo.method(x = "blah")  ~~> foo.applyDynamicNamed("method")(("x", "blah"))
 *  foo.method(x = 1, 2)    ~~> foo.applyDynamicNamed("method")(("x", 1), ("", 2))
 *  foo.field           ~~> foo.selectDynamic("field")
 *  foo.varia = 10      ~~> foo.updateDynamic("varia")(10)
 *  foo.arr(10) = 13    ~~> foo.selectDynamic("arr").update(10, 13)
 *  foo.arr(10)         ~~> foo.applyDynamic("arr")(10)
```

So I propose two possibilities:

1. Access to the group i.e. CONFIG.ftp
2. Access to the property CONFIG.ftp("name")

Those methods are generic, so you can receive T which would force to do a pattern matching

I would prefer something more like

```val mongodbURI : String = configuration.getString(s"$env.mongodbURI").getOrElse(throw new IllegalArgumentException("mongodbURI is not defined"))```

The approach that I use is https://github.com/typesafehub/config


In terms of performance I dont think that we need a cache or similiar. The reason is because in my components I build all the singletons in the start up 
and inject the properties needed in the constructor. Something similar to Spring with application context.

Each property is read just once and assign to a variable. This variable will be the one used.

```

trait Environment extends RunMode{
  info(s"loading the environment with run mode $env")
  val mongodbURI : String = configuration.getString(s"$env.mongodbURI").getOrElse(throw new IllegalArgumentException("mongodbURI is not defined"))
  val mongodbDatabaseName : String = configuration.getString(s"$env.mongodbDatabaseName").getOrElse(throw new IllegalArgumentException("mongodbDatabaseName is not defined"))
  val host : String = configuration.getString(s"$env.host").getOrElse(throw new IllegalArgumentException("host is not defined"))
  val time: Option[DateTime] =  configuration.getString(s"$env.time").map(DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").parseDateTime(_))
  val auth: String = configuration.getString(s"$env.auth").getOrElse(throw new IllegalArgumentException("auth service is not defined"))
}

object Environment extends Environment
```

Therefore if there is something wrong in the configuration then the start up process will raise a RuntimeException and will finish.


Note:
-----

There is no validation and some regular expressions need to be improved

 
 
