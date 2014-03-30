name := "cabinBooking"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "net.sf.flexjson" % "flexjson" % "2.1"
)     


play.Project.playJavaSettings

playAssetsDirectories <+= baseDirectory / "app-ui"