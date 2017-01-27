package com.atomist.rug.kind.gradle

/**
  * 'Enumeration' representing the allowable components that can be published via the gradle maven publish
  * plugin
  */

class PublicationComponents

object PublicationComponents {

  sealed abstract class Component(val name: String)

  case object JAVA extends Component("java")

  case object WEB extends Component("web")

}
