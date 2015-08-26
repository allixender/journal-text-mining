name := """lucene-test-scala"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

val luceneVersion = "4.9.0"
val n52xmlVersion = "2.1.0"
val xmlBeansVersion = "2.6.0"
val geotoolsVersion = "13.1"


libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.jsoup" % "jsoup" % "1.7.2",
  "org.apache.xmlbeans" % "xmlbeans" % xmlBeansVersion,
  "org.apache.xmlbeans" % "xmlbeans-xpath" % xmlBeansVersion,
  "org.n52.sensorweb" % "52n-xml-csw-v202" % n52xmlVersion,
  "info.smart-project" % "iso19139-xml" % "0.0.9",
  "org.apache.jena" % "apache-jena-libs" % "2.12.1",
  "net.sf.opencsv" % "opencsv" % "2.3",
  "org.geotools" % "gt-epsg-hsql" % geotoolsVersion,
  "org.geotools" % "gt-referencing" % geotoolsVersion,
  "com.vividsolutions" % "jts" % "1.13",
  "commons-io" % "commons-io" % "2.2",
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.5" exclude("org.xerial.snappy", "snappy-java"),
  "org.xerial.snappy"       	% "snappy-java"           % "1.1.1.7"
)

pipelineStages := Seq(rjs, digest, gzip)
		
resolvers += "smart releases" at "http://dev.smart-project.info/artifactory/libs-release/"

resolvers += "smart snapshots" at "http://dev.smart-project.info/artifactory/libs-snapshot/"

resolvers += "Official Maven Repo" at "http://repo1.maven.org/maven2/"

resolvers += "maven2 central" at "http://central.maven.org/maven2/"

resolvers += "52North Releases" at "http://52north.org/maven/repo/releases/"

resolvers += "52North Snapshots" at "http://52north.org/maven/repo/snapshots/"

resolvers += Resolver.sonatypeRepo("releases")

resolvers += Resolver.sonatypeRepo("snaphots")

resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

scalacOptions in ThisBuild ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  "-Xlint", // recommended additional warnings
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
  "-Ywarn-inaccessible",
  "-Ywarn-dead-code",
  "-language:reflectiveCalls"
)

javacOptions in Compile ++= Seq(
  "-encoding", "UTF-8",
  "-source", "1.8",
  "-target", "1.8",
  "-g",
  "-Xlint:-path",
  "-Xlint:deprecation",
  "-Xlint:unchecked"
)
