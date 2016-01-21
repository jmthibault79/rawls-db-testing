package org.broadinstitute.dsde.rawls.dbtest.generator

import java.util.UUID

import org.broadinstitute.dsde.rawls.dbtest.json.{Workspace, Entity}

case class Generator(levelCount: Int, entitiesPerLevel: Int, attributesPerEntity: Int) {

  def immediateChildren(parentName: String, candidates: Seq[Entity]): Map[String, String] = {
    // immediate children have the pattern PARENT_CHILDNUMBER
    val reg = (parentName + """_(\d+)""").r

    val matched = candidates.flatMap { ent =>
      val iter = reg.findAllIn(ent.name).matchData

      if (iter.hasNext) {
        val capture = iter.next.group(1)
        Some(s"child_${capture}" -> ent.name)
      }
      else {
        None
      }
    }
    matched.toMap
  }

  def childEntities(childLevel: Int, parentName: String): Seq[Entity] = {
    (1 to entitiesPerLevel) flatMap { idx: Int =>
      val name = s"${parentName}_$idx"
      val entityType = s"Level $childLevel Entity"
      val attrs = Generator.genAttrs(attributesPerEntity)

      val leafNode = (childLevel == levelCount)
      if (leafNode) {
        Seq(Entity(name, entityType, attrs, Map()))
      }
      else {
        val descendants = childEntities(childLevel + 1, name)
        val childRefs = immediateChildren(name, descendants)
        descendants :+ Entity(name, entityType, attrs, childRefs)
      }
    }
  }
}

object Generator {
  def genAttrs(count: Int) = (1 to count).map { attrIdx: Int => s"UUID $attrIdx" -> UUID.randomUUID.toString }.toMap

  // generates $wsCount workspaces, each with $levelCount levels of Entities, each level having $entitiesPerLevel
  def multiWsWithMultiLevelEntities(wsCount: Int, levelCount: Int, entitiesPerLevel: Int, attributesPerEntity: Int): Seq[Workspace] = {
    val generator = Generator(levelCount, entitiesPerLevel, attributesPerEntity)

    (1 to wsCount) map { idx: Int =>
      val name = s"WS$idx"
      val ents = generator.childEntities(1, name)
      Workspace(name, genAttrs(attributesPerEntity), ents)
    }
  }

  def multiWsWithMultiLevelEntities(args: Int*): Seq[Workspace] = {
    multiWsWithMultiLevelEntities(args(0), args(1), args(2), args(3))
  }

}
