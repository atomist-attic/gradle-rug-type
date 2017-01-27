package com.atomist.rug.kind.gradle

import com.atomist.rug.kind.core.ProjectMutableView
import com.atomist.source.StringFileArtifact
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import org.mockito.Mockito._

class GradleMutableViewTest extends FlatSpec
  with Matchers
  with BeforeAndAfter {

  var mockProjectMutableView: ProjectMutableView = _

  before {

    mockProjectMutableView = mock(classOf[ProjectMutableView])
  }

  behavior of "A GradleMutableView when updating publications of a gradle file w/o any existing publications"

  val gradleFileWithoutPublish =
    """
      |group 'com.xyz'
      |version '1.0.0-SNAPSHOT'
      |
      |apply plugin: 'java'
      |
      |dependencies {
      |
      |    compile 'com.amazonaws:aws-lambda-java-core:1.1.0'
      |    compile 'commons-io:commons-io:2.5'
      |}
      |
      |task fatJar(type: Jar) {
      |    manifest {
      |        attributes 'Implementation-Title': 'Lambda Fat Jar',
      |                'Implementation-Version': version
      |    }
      |
      |    baseName = jar.baseName
      |    appendix = jar.appendix + '-all'
      |
      |    from {
      |
      |        configurations.compile.collect {
      |
      |            it.isDirectory() ? it : zipTree(it)
      |        }
      |    } {
      |        exclude 'scala/**/*.class'
      |    }
      |    with jar
      |}
      |
      |jar.dependsOn('fatJar')
    """.stripMargin

  it should "apply the maven-publish plugin" in {

    val artifactSource = StringFileArtifact("build.gradle", gradleFileWithoutPublish)

    val gradleMutableView = new GradleMutableView(artifactSource, mockProjectMutableView)

    gradleMutableView.updatePublications(PublicationComponents.JAVA)

    gradleMutableView.content should include ("apply plugin: 'maven-publish'")
  }
}
