import java.io.{File, FileInputStream}

/* mecha-core */
def versionFromFile(filename: String): String = {
  val fis = new FileInputStream(filename)
  val props = new java.util.Properties()
  try props.load(fis)
  finally fis.close()

  val major = props.getProperty("mecha_major")
  val minor = props.getProperty("mecha_minor")
  s"$major.$minor"
}

val frameworkVersion = baseDirectory { dir =>
  versionFromFile(dir + File.separator + "version.conf")
}


val mechaScalaVersion = "2.12.8"

val mechaSettings = Seq(
  sbtPlugin := true,
  name := "mecha",
  scalaVersion := mechaScalaVersion,
  version := frameworkVersion.value,
  organization := "com.storm-enroute",
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.2.1",
    "commons-io" % "commons-io" % "2.4",
    "com.decodified" %% "scala-ssh" % "0.9.0",
    "com.github.pathikrit" %% "better-files" % "3.8.0",
    "org.specs2" %% "specs2-core" % "4.3.4" % "test",
    "org.specs2" %% "specs2-junit" % "4.3.4" % "test",
    "junit" % "junit" % "4.12" % "test"
  ),
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if(isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra :=
    <url>http://storm-enroute.com/</url>
      <licenses>
        <license>
          <name>BSD-style</name>
          <url>http://opensource.org/licenses/BSD-3-Clause</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:storm-enroute/mecha.git</url>
        <connection>scm:git:git@github.com:storm-enroute/mecha.git</connection>
      </scm>
      <developers>
        <developer>
          <id>axel22</id>
          <name>Aleksandar Prokopec</name>
          <url>http://axel22.github.com/</url>
        </developer>
      </developers>
)

lazy val mechaRoot = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(mechaSettings ++ Seq(
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false
  ))

// set environment variables to publish
// in newer SBT versions, this apparently has to go to `build.sbt`

//{
//  val publishUser = "SONATYPE_USER"
//  val publishPass = "SONATYPE_PASS"
//  val userPass = for {
//    user <- sys.env.get(publishUser)
//    pass <- sys.env.get(publishPass)
//  } yield (user, pass)
//  val publishCreds: Seq[Setting[_]] = Seq(userPass match {
//    case Some((user, pass)) =>
//      println(s"Username and password for Sonatype picked up: '$user', '${if (pass != "") "******" else ""}'")
//      credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
//    case None =>
//      // prevent publishing
//      val errorMessage =
//        "Publishing to Sonatype is disabled since the \"" +
//        publishUser + "\" and/or \"" + publishPass + "\" environment variables are not set."
//      println(errorMessage)
//      skip := true
//  })
//  publishCreds
//}
