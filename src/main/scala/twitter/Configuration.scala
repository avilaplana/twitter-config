package twitter

import scala.language.dynamics
import Group._
import Property._

class Configuration(private val configMap: Map[String, Map[String, Any]]) extends Dynamic {

  def selectDynamic(group: String) = configMap.get(group)

  def applyDynamic(group: String)(property: String): Any = {
    for {
      g <- configMap.get(group)
      p <- g.get(property)
    } yield p
  }
}

object Configuration {

  private type GroupOfProperties = Map[Group, Seq[Property[_]]]

  private[twitter] def parseFile(lines: Seq[String] , environments: String*): Map[String, Map[String, Any]] = {
    simplifyMap {
      filterByEnvironment(
        groupLines(
          parseLines(lines)
        )
      )(environments: _*)
    }
  }

  private val simplifyMap: GroupOfProperties => Map[String, Map[String, Any]] = {
    group => group.map {
      case (g, properties) => (g.value, properties.map(p => (p.key, p.value)).toMap)
    }
  }

  private[twitter] def parseLines(lines: Seq[String]): Seq[Line] = lines map {
    l =>
      asGroup(l) orElse asBoolean(l) orElse asString(l) orElse asArray(l) orElse asNumber(l) orElse asPath(l) getOrElse Discardable
  }

  private[twitter] def groupLines(lines: Seq[Line]): GroupOfProperties = {
    lines.foldLeft(Map.empty[Group, Seq[Property[_]]]) {
      (groups, l) => l match {
        case group@Group(_) => groups + (group -> Seq.empty[Property[_]])
        case property@Property(_, _, _) => groups + (groups.last._1 -> (groups.last._2 :+ property))
        case _ => groups
      }
    }
  }

  private[twitter] def filterByEnvironment(groups: GroupOfProperties)(env: String*): GroupOfProperties = {
    groups.map {
      case (g, properties) =>
        val propGroupedByKey = properties.groupBy(_.key)
        val propertiesFiltered = propGroupedByKey.map {
          case (key, p) =>
            val propsFilteredByEnv = p.filter {
              _.environment match {
                case Some(e) if env.contains(e) => true
                case _ => false
              }
            }
            val defaultProperty = p.filter(_.environment.isEmpty).headOption
            (propsFilteredByEnv.lastOption, defaultProperty) match {
              case (Some(p), _) => println(p);(key, Seq(p))
              case (_, Some(p)) => (key, Seq(p))
              case _ => (key, Seq.empty[Property[_]])
            }
        }.filter(_._2.size > 0)

        (g, propertiesFiltered.values.flatten.toSeq)
    }
  }


  def load_config(filePath: String, overrides: String*) = {
    val fileLines = io.Source.fromInputStream(getClass.getResourceAsStream(filePath)).getLines().toSeq
    val newOverrides = overrides.map(o => if (o.matches("^<.*>$")) o else s"<$o>")

    new Configuration(parseFile(fileLines, newOverrides: _*))
  }
}




