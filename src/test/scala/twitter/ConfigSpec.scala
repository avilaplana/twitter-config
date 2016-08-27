package twitter

import org.scalatest.{MustMatchers, WordSpec}

class ConfigSpec extends WordSpec with MustMatchers {

  //  val config = Configuration.load_config("/exercise.conf", "ubuntu", "production")
  //
  //  "config.common.paid_users_size_limit" should {
  //    "return the number 2147483648" in {
  //      config.common("paid_users_size_limit") mustBe Some(2147483648L)
  //    }
  //  }
  //
  //  "config.ftp.name" should {
  //    "return the string 'hello there, ftp uploading'" in {
  //      config.ftp("name") mustBe Some("hello there, ftp uploading")
  //    }
  //  }
  //
  //  "config.http.params" should {
  //    "return an array with the values 'array', 'of' and 'values'" in {
  //      config.http("params") mustBe Some(Seq("array", "of", "values"))
  //    }
  //  }
  //
  //  "config.ftp.lastname" should {
  //    "return None when there is no value define" in {
  //      config.ftp("lastname") mustBe None
  //    }
  //  }
  //
  //  "config.ftp.enabled" should {
  //    "return boolean false" in {
  //      config.ftp("enabled") mustBe Some(false)
  //    }
  //  }
  //
  //  "config.ftp[‘path’]" should {
  //    "return the string '/etc/var/uploads/'" in {
  //      config.ftp("path") mustBe Some("/etc/var/uploads")
  //    }
  //  }
  //
  //  "config.ftp" should {
  //    "return the map with the following values ‘name’ => 'hello there, ftp uploading', ‘path’ => '/etc/var/uploads', ‘enabled’ => False }" in {
  //      config.ftp mustBe Some(Map("name" -> "hello there, ftp uploading", "path" -> "/etc/var/uploads", "enabled" -> false))
  //    }
  //  }


  "parseLines" should {
    "return a list of Line objects representing the lines" in {

      Configuration.parseLines(
        Seq(
          "[common]",
          "basic_size_limit = 26214400",
          "path = /srv/var/tmp",
          "path<itscript> = /srv/tmp",
          "name = \" hello there, ftp uploading\"",
          "enabled = no",
          "params = array,of,values",
          "; This is a comment"
        )
      ) mustBe Seq(
        Group("common"),
        Property("basic_size_limit", 26214400L, None),
        Property("path", "/srv/var/tmp", None),
        Property("path", "/srv/tmp", Some("<itscript>")),
        Property("name", " hello there, ftp uploading", None),
        Property("enabled", false, None),
        Property("params", Seq("array", "of", "values"), None),
        Discardable
      )
    }
  }


  "groupLines" should {
    "return a map of groups with their properties" in {

      Configuration.groupLines(
        Seq(
          Group("common"),
          Property("basic_size_limit", 26214400L, None),
          Property("path", "/srv/var/tmp", None),
          Group("http"),
          Property("name", "http uploading", None),
          Property("path", "/tmp", None)
        )
      ) mustBe Map(
        Group("common") -> Seq(
          Property("basic_size_limit", 26214400L, None),
          Property("path", "/srv/var/tmp", None)
        ),
        Group("http") -> Seq(
          Property("name", "http uploading", None),
          Property("path", "/tmp", None))
      )
    }
  }

  "filterByEnvironment" should {
    "return a map of groups with properties filtered by environment <env1>" in {

      Configuration.filterByEnvironment(
        Map(Group("common") -> Seq(
          Property("basic_size_limit", 26214400L, None),
          Property("path", "/srv/var/tmp", None),
          Property("path", "/srv/var/tmp", Some("<env1>")))
        )
      )("<env1>") mustBe Map(
        Group("common") -> Seq(
          Property("basic_size_limit", 26214400L, None),
          Property("path", "/srv/var/tmp", Some("<env1>"))
        )
      )
    }

    "return a map of groups with default property" in {

      Configuration.filterByEnvironment(
        Map(Group("common") -> Seq(
          Property("basic_size_limit", 26214400L, None),
          Property("path", "/srv/var/tmp", None),
          Property("path", "/srv/var/tmp", Some("<env1>")))
        )
      )("<env2>") mustBe Map(
        Group("common") -> Seq(
          Property("basic_size_limit", 26214400L, None),
          Property("path", "/srv/var/tmp", None)
        )
      )
    }

    "return a map of groups removing properties when there is default and no match with overrides environment" in {

      Configuration.filterByEnvironment(
        Map(Group("common") -> Seq(
          Property("basic_size_limit", 26214400L, None),
          Property("path", "/srv/var/tmp", Some("<env3>")))
        )
      )("<env1>", "<env2>") mustBe Map(
        Group("common") -> Seq(
          Property("basic_size_limit", 26214400L, None)
        )
      )
    }

    "return a map of groups for enviornments '<ubuntu>' and '<production>'" in {
      Configuration.filterByEnvironment(
        Map(
          Group("ftp") -> Seq(
            Property("name", "hello there, ftp uploading", None),
            Property("path", "/srv/var/tmp/", Some("<production>")),
            Property("path", "/srv/uploads/", Some("<staging>")),
            Property("path", "/etc/var/uploads", Some("<ubuntu>")),
            Property("enable", false, None))
        )
      )("<ubuntu>", "<production>") mustBe Map(
        Group("ftp") -> Seq(
          Property("name", "hello there, ftp uploading", None),
          Property("enable", false, None),
          Property("path", "/etc/var/uploads", Some("<ubuntu>"))
        )
      )
    }
  }


}
