name := "fabut4scala"

organization := "eu.execom"

version := "0.4.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-lang"      % "scala-reflect"         % "2.11.8",

  "junit"               % "junit"                 % "4.12",

  "org.apache.commons"  % "commons-lang3"         % "3.1",

  "org.apache.commons"  % "commons-email"         % "1.3.3",

  "org.subethamail"     % "subethasmtp-wiser"     % "1.2",

  "org.clapper"         % "grizzled-slf4j_2.11"   % "1.0.2",

  "ch.qos.logback"      % "logback-classic"       % "1.1.2",

  "ch.qos.logback"      % "logback-core"          % "1.1.2"
)



