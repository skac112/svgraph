enablePlugins(ScalaJSPlugin)
name := "svgraph"
scalaVersion := "2.12.4" // or any other Scala version >= 2.10.2
libraryDependencies += "skac" %%% "euler" % "0.3.0-SNAPSHOT"
libraryDependencies += "skac" %%% "vgutils" % "1.0.0-SNAPSHOT"
libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.9.2"
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1"
// This is an application with a main method
scalaJSUseMainModuleInitializer := true
