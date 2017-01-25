package com.atomist.rug.kind.gradle

import com.atomist.rug.kind.core.ProjectMutableView
import com.atomist.rug.kind.groovy.GroovySourceMutableView
import com.atomist.rug.spi.TerminalView
import com.atomist.source.FileArtifact

object GradleMutableView

trait GradleMutableViewNonMutatingFunctions

trait GradleMutableViewMutatingFunctions

class GradleMutableView(

                               originalBackingObject: FileArtifact,
                               parent: ProjectMutableView)
  extends GroovySourceMutableView(originalBackingObject, parent)
    with TerminalView[FileArtifact]
    with GradleMutableViewNonMutatingFunctions
    with GradleMutableViewMutatingFunctions {

  override protected def currentContent: String = content
}
