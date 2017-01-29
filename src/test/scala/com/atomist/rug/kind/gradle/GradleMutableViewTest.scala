package com.atomist.rug.kind.gradle

import com.atomist.rug.kind.core.ProjectMutableView
import com.atomist.source.StringFileArtifact
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import org.mockito.Mockito._
import io.Source._

class GradleMutableViewTest extends FlatSpec
  with Matchers
  with BeforeAndAfter {

  var mockProjectMutableView: ProjectMutableView = _

  before {

    mockProjectMutableView = mock(classOf[ProjectMutableView])
  }

  behavior of "A GradleMutableView when updating java publications of a gradle file w/o any existing publications"

  val gradleFileWithoutPublish = fromInputStream(getClass.getClassLoader.getResourceAsStream("withoutPublish-build.gradle")).mkString

  it should "apply the maven-publish plugin" in {

    updatePublicationAndAssertMavenPublishPlugin(gradleFileWithoutPublish)
  }

  it should "apply maven-publish plugin in absence of any other plugin" in {

    val gradleFileNoPlugins = gradleFileWithoutPublish.linesWithSeparators
      .filter(line => {
        !line.contains("apply plugin:")
      }).mkString

    updatePublicationAndAssertMavenPublishPlugin(gradleFileNoPlugins)
  }

  it should "add the publishing configuration" in {

    val gradleMutableView = createGradleMutableView(gradleFileWithoutPublish)

    gradleMutableView.updatePublications(PublicationComponents.JAVA)

    gradleMutableView.content should include  ("""publishing {
                                                |    publications {
                                                |        mavenJava(MavenPublication) {
                                                |
                                                |            from components.java
                                                |        }
                                                |    }
                                                |}""".stripMargin)
  }

  it should "throw an exception for unsupported web component" in {

    val gradleMutableView = createGradleMutableView(gradleFileWithoutPublish)

    an [IllegalArgumentException] should be thrownBy gradleMutableView.updatePublications(PublicationComponents.WEB)
  }

  behavior of "A GradleMutableView when updating java publications of a gradle file with existing publications"

  val gradleFileWithPublish = fromInputStream(getClass.getClassLoader.getResourceAsStream("withPublish-build.gradle")).mkString

  it should "not add another maven-publish plugin" in {

    val gradleMutableView = createGradleMutableView(gradleFileWithPublish)

    gradleMutableView.updatePublications(PublicationComponents.JAVA)

    gradleMutableView.content should equal (gradleFileWithPublish)
  }

  /* ================= Utility Functions =========================================================== */

  private def updatePublicationAndAssertMavenPublishPlugin(gradleContent: String): Unit = {

    val gradleMutableView = createGradleMutableView(gradleContent)

    gradleMutableView.updatePublications(PublicationComponents.JAVA)

    gradleMutableView.content should include("apply plugin: 'maven-publish'")
  }

  private def createGradleMutableView(gradleContent: String) : GradleMutableView = {

    val artifactSource = StringFileArtifact("build.gradle", gradleContent)

    new GradleMutableView(artifactSource, mockProjectMutableView)
  }
}
