name := "cabinBooking"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "net.sf.flexjson" % "flexjson" % "2.1", 
  "org.bouncycastle" % "bcprov-jdk15on" % "1.50"
)     

play.Project.playJavaSettings
