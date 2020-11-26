import java.net.URL
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  java
  jacoco
  id("org.openstreetmap.josm") version "0.7.1"
}


repositories {
  jcenter()
}
dependencies {
  testImplementation("org.openstreetmap.josm:josm-unittest:SNAPSHOT"){isChanging=true}
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
  // For parsing gtfs files
  implementation("com.opencsv:opencsv:5.3")
}

tasks.withType(JavaCompile::class) {
  options.compilerArgs.addAll(
    arrayOf("-Xlint:all", "-Xlint:-serial")
  )
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
  withSourcesJar()
  withJavadocJar()
}

josm {
  manifest {
  }
  i18n {
  }
}

tasks.jacocoTestReport {
  reports {
    xml.isEnabled = true
    html.isEnabled = true
  }
  dependsOn(tasks.test)
  tasks.check.get().dependsOn(this)
}
