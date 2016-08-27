# Exercise - Taxonomy

Technologies:
```
1. JDK 1.8
2. Scala 2.11
3. Sbt 0.13.8
4. Scalatest
```

Run the test
```
sbt test
```


```
Config Challenge
Please spend 3 hours on:
Every large software project has its share of configuration files to control settings, execution, etc. Let’s contemplate a config file format that looks a lot like standard PHP .ini files, but with a few tweaks.
A config file will appear as follows:

[common]
basic_size_limit = 26214400
student_size_limit = 52428800
paid_users_size_limit = 2147483648
path = /srv/var/tmp/
path<itscript> = /srv/tmp/

[ftp]
name = "hello there, ftp uploading"
path = /tmp/
path<production> = /srv/var/tmp/
path<staging> = /srv/uploads/
path<ubuntu> = /etc/var/uploads
enabled = no
; This is a comment

[http]
name = "http uploading"
path = /tmp/
path<production> = /srv/var/tmp/
path<staging> = /srv/uploads/; This is another comment
params = array,of,values

Where "[group]" denotes the start of a group of related config options, setting = value denotes a standard setting name and associated default value, and setting<override> = value2 denotes the value for the setting if the given override is enabled. If multiple enabled overrides are defined on a setting, the one defined last will have priority.
Assignment
Your task is to write a Python function: def load_config(file_path, overrides=[]) that parses this format and returns an object that can be queried as follows. Note that overrides can be passed either as strings or as symbols: CONFIG = load_config("/srv/settings.conf", ["ubuntu", "production"])

>>> CONFIG.common.paid_users_size_limit
# returns 2147483648
> CONFIG.ftp.name
# returns "hello there, ftp uploading"
>>> CONFIG.http.params
# returns ["array", "of", "values"]
> CONFIG.ftp.lastname
# returns None
> CONFIG.ftp.enabled
# returns false (permitted bool values are "yes", "no", "true", "false", 1, 0)
> CONFIG.ftp[‘path’] # returns "/etc/var/uploads"
> CONFIG.ftp
# returns a dict: # { # ‘name’ => "hello there, ftp uploading", # ‘path’ => "/etc/var/uploads", # ‘enabled’ => False # }

We'll be testing using Python 2.7.10 unless you tell us otherwise; if you have any features you think are important from a later version, please let us know what to look for!
Please submit your code as plain text (.py files). Include any tests you write or notes — we'd love to see your thought process as we review your implementation. Think of it like a code review: what context should we have to best understand your work? Make sure to remove your name or any other identifying information from files before submitting.
Design Considerations
load_config() will be called at boot time, and thus should be as fast as possible. Conf files can get quite lengthy - there can be an arbitrary number of groups and number of settings within each group.
CONFIG will be queried throughout the program’s execution, so each query should be very fast as well.
Certain queries will be made very often (thousands of times), others pretty rarely.
If the conf file is not well-formed, it is acceptable to print an error and exit from within load_config(). Once the object is returned, however, it is not permissible to exit or crash no matter what the query is. Returning None is acceptable, however.
```