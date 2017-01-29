package com.atomist.rug.kind.gradle

import com.atomist.rug.kind.core.ProjectMutableView
import com.atomist.rug.kind.groovy.GroovySourceMutableView
import com.atomist.rug.spi.TerminalView
import com.atomist.source.FileArtifact
import scala.compat.Platform.EOL

object GradleMutableView

trait GradleMutableViewNonMutatingFunctions

trait GradleMutableViewMutatingFunctions {

  def updatePublications(component: PublicationComponents.Component): Unit
}

class GradleMutableView(

                         originalBackingObject: FileArtifact,
                         parent: ProjectMutableView)
  extends GroovySourceMutableView(originalBackingObject, parent)
    with TerminalView[FileArtifact]
    with GradleMutableViewNonMutatingFunctions
    with GradleMutableViewMutatingFunctions {

  val PLUGIN_LINE = "apply plugin:"
  val PUBLISH_PLUGIN_LINE = "apply plugin: \'maven-publish\'" + EOL
  val PUBLICATION_CONFIG =
    """publishing {
      |    publications {
      |        mavenJava(MavenPublication) {
      |
      |            from components.java
      |        }
      |    }
      |}""".stripMargin
  val PUBLISH_PLUGIN_REGEX = "apply[\\s]+plugin:[\\s]*'maven-publish'".r
  val JAVA_PUBLISHING_CONFIG_REGEX = "publishing[\\s]*\\{.*from[\\s]+components\\.java".r

  private var gradleContent = originalBackingObject.content

  private var publicationComponent: PublicationComponents.Component = _

  override protected def currentContent: String = gradleContent

  override def updatePublications(component: PublicationComponents.Component): Unit = {

    component match {
      case PublicationComponents.JAVA =>
        publicationComponent = component

        addPlugin

        addPublicationConfig

      case PublicationComponents.WEB => throw new IllegalArgumentException("Web type not supported")
    }

  }

  private def addPlugin(): Unit = {

    PUBLISH_PLUGIN_REGEX.findFirstIn(gradleContent) match {

      case None =>

        val currentPlugins = gatherLinesContaining(line => {
          line.contains(PLUGIN_LINE)
        })

        val gradleBuildWithoutPlugins = gatherLinesContaining(line => {
          !line.contains(PLUGIN_LINE)
        })

        val pluginsWithMavenPublish = currentPlugins :+ PUBLISH_PLUGIN_LINE

        val newGradleContent = pluginsWithMavenPublish ++ gradleBuildWithoutPlugins

        gradleContent = newGradleContent.mkString

      case Some(x: String) => // do nothing
    }
  }

  private def addPublicationConfig(): Unit = {

    JAVA_PUBLISHING_CONFIG_REGEX.findFirstIn(gradleContent.lines.mkString) match {

      case None => gradleContent = gradleContent ++ (EOL * 2) ++ PUBLICATION_CONFIG
      case Some(x: String) => //do noting
    }

  }

  private def gatherLinesContaining(predicate: String => Boolean): List[String] = {

    gradleContent.linesWithSeparators.filter(predicate).toList
  }


}
