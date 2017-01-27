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

  private var gradleContent = originalBackingObject.content

  override protected def currentContent: String = gradleContent

  override def updatePublications(component: PublicationComponents.Component): Unit = {

    val currentPlugins = gradleContent.linesWithSeparators
      .filter(line => {
        line.contains(PLUGIN_LINE)
      })
      .toList

    val gradleBuildWithoutPlugins = gradleContent.linesWithSeparators
      .filter(line => {
        !line.contains(PLUGIN_LINE)
      })
      .toList

    val pluginsWithMavenPublish = currentPlugins :+ PUBLISH_PLUGIN_LINE

    val newGradleContent = pluginsWithMavenPublish ++ gradleBuildWithoutPlugins

    gradleContent = newGradleContent.mkString
  }
}
