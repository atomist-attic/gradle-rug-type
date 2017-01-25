package com.atomist.rug.kind.gradle

import com.atomist.rug.kind.core.ProjectMutableView
import com.atomist.rug.runtime.rugdsl.Evaluator
import com.atomist.source.file.FileSystemArtifactSource
import com.atomist.source.{FileArtifact, StringFileArtifact}
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class GradleTypeTest extends FlatSpec
  with BeforeAndAfter
  with Matchers {

  var mockEvaluator: Evaluator = _
  var mockMutableView: ProjectMutableView = _
  var mockArtifactSource: FileSystemArtifactSource = _
  var gradleType: GradleType = _

  before {

    mockEvaluator = mock(classOf[Evaluator])
    mockMutableView = mock(classOf[ProjectMutableView])
    mockArtifactSource = mock(classOf[FileSystemArtifactSource])

    gradleType = new GradleType(mockEvaluator)

    val fileArtifacts = Seq[FileArtifact](
      StringFileArtifact.apply("/foo/build.gradle", """apply plugin: 'scala'""")
    )

    when(mockArtifactSource.allFiles).thenReturn(fileArtifacts)
    when(mockMutableView.currentBackingObject).thenReturn(mockArtifactSource)
  }

  "A GradleType" should "describe itself as \'Gradle build file\'" in {

    val expected = "Gradle build file"
    val gradleTypeDesc = gradleType.description

    gradleTypeDesc should equal(expected)
  }
}
