enablePlugins(ScalaJSPlugin)
name := "svgraph"
version := "0.1.0-SNAPSHOT"
organization := "skac"
scalaVersion := "2.12.4" // or any other Scala version >= 2.10.2
libraryDependencies += "skac" %%% "euler" % "0.5.0-SNAPSHOT"
libraryDependencies += "skac" %%% "vgutils" % "0.1.0-SNAPSHOT"
libraryDependencies += "skac" %%% "scalajs-jquery" % "1.0.0-SNAPSHOT"
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.5"
// This is an application with a main method
scalaJSUseMainModuleInitializer := true
