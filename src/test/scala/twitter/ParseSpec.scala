package twitter

import org.scalatest.{MustMatchers, WordSpec}

class ParseSpec extends WordSpec with MustMatchers {

  "Group" should {
    "return some Group'common' when the value is '[common]'" in {
      Group.asGroup("[common]") mustBe Some(Group("common"))
    }

    "return a None when the value is 'whatever'" in {
      Group.asGroup("whatever") mustBe None
    }
  }


  "asString" should {
    "return some StringProperty with key 'key1' and value 'this is an example' when the value is 'key1 = \"this is an example\"'" in {
      Property.asString("key = \"this is an example\"") mustBe Some(Property("key", "this is an example", None))
    }

    "return some StringProperty with key 'key1', value 'this is an example' and environment 'env1' when the value is 'key1<env1> = \"this is an example\"'" in {
      Property.asString("key1<env1> = \"this is an example\"") mustBe Some(Property("key1", "this is an example", Some("<env1>")))
    }

    "return some StringProperty with key 'key1' and value 'this is an example' when the value is 'key1 = \"this is an example\";some comment'" in {
      Property.asString("key1 = \"this is an example\";some comment") mustBe Some(Property("key1", "this is an example", None))
    }
    "return None when the value is 'key1 = whatever'" in {
      Property.asString("key1 = whatever") mustBe None
    }
  }

  "asNumber" should {
    "return some NumberProperty with key 'key1' and value 12345678 when the value is 'key1 = 12345678'" in {
      Property.asNumber("key = 12345678") mustBe Some(Property("key", 12345678, None))
    }

    "return some NumberProperty with key 'key1', value 12345678 and environment <env1> when the value is 'key1<env1> = 12345678'" in {
      Property.asNumber("key1<env1> = 12345678") mustBe Some(Property("key1", 12345678, Some("<env1>")))
    }

    "return some NumberProperty with key 'key1' and value 12345678 when the value is 'key1 = 12345678;some comment'" in {
      Property.asNumber("key = 12345678;some comment") mustBe Some(Property("key", 12345678, None))
    }

    "return None when the value is 'key1 = whatever'" in {
      Property.asNumber("key1 = whatever") mustBe None
    }
  }

  "asBoolean" should {
    "return some Property with key 'key1' and value true when the value is 'key1 = true'" in {
      Property.asBoolean("key1 = true") mustBe Some(Property("key1", true, None))
    }

    "return some Property with key 'key1' and value true when the value is 'key1 = 1'" in {
      Property.asBoolean("key1 = 1") mustBe Some(Property("key1", true, None))
    }

    "return some Property with key 'key1' and value true when the value is 'key1 = yes'" in {
      Property.asBoolean("key1 = yes") mustBe Some(Property("key1", true, None))
    }

    "return some Property with key 'key1' and value false when the value is 'key1 = false'" in {
      Property.asBoolean("key1 = false") mustBe Some(Property("key1", false, None))
    }

    "return some Property with key 'key1' and value false when the value is 'key1 = 0'" in {
      Property.asBoolean("key1 = 0") mustBe Some(Property("key1", false, None))
    }

    "return some Property with key 'key1' and value false when the value is 'key1 = no'" in {
      Property.asBoolean("key1 = no") mustBe Some(Property("key1", false, None))
    }

    "return some Property with key 'key1', value false and environment 'env1' when the value is 'key1<env1> = no'" in {
      Property.asBoolean("key1<env1> = no") mustBe Some(Property("key1", false, Some("<env1>")))
    }

    "return some Property with key 'key1' and value true when the value is 'key1 = true;some comment'" in {
      Property.asBoolean("key1 = true;some comment") mustBe Some(Property("key1", true, None))
    }

    "return None when the value is 'key1 = whatever'" in {
      Property.asBoolean("key1 = whatever") mustBe None
    }
  }

  "asArray" should {
    "return some ArrayProperty with key 'key1' and value ['this','is','example'] when the value is 'key1 = this,is,example'" in {
      Property.asArray("key1 = this,is,example") mustBe Some(Property("key1", Seq("this", "is", "example"), None))
    }

    "return some ArrayProperty with key 'key1', value ['this','is','example'] and environment '<env1>' when the value is 'key1<env1> = this,is,example'" in {
      Property.asArray("key1<env1> = this,is,example") mustBe Some(Property("key1", Seq("this", "is", "example"), Some("<env1>")))
    }

    "return some ArrayProperty with key 'key1' and value ['this','is','example'] when the value is 'key1 = this,is,example;some comment'" in {
      Property.asArray("key1 = this,is,example;some comment") mustBe Some(Property("key1", Seq("this", "is", "example"), None))
    }

    "return None when the value is 'key1 = whatever'" in {
      Property.asArray("key1 = whatever") mustBe None
    }
  }


  "asPath" should {
    "return some Property with key 'key1' and value '/this/is/path' when the value is 'key1 = /this/is/path'" in {
      Property.asPath("key1 = /this/is/path") mustBe Some(Property("key1", "/this/is/path", None))
    }

    "return some Property with key 'key1', value 'this/is/path' and environment '<env1>'when the value is 'key1<env1> = this/is/path'" in {
      Property.asPath("key1<env1> = this/is/path") mustBe Some(Property("key1", "this/is/path", Some("<env1>")))
    }

    "return some Property with key 'key1', value '/this/is/path/' and environment '<env1>'when the value is 'key1<env1> = /this/is/path/'" in {
      Property.asPath("key1<env1> = /this/is/path/") mustBe Some(Property("key1", "/this/is/path/", Some("<env1>")))
    }

    "return None when the value is 'key1 = whatever'" in {
      Property.asPath("key1 = whatever") mustBe None
    }
  }


}
