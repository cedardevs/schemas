plugins {
  groovy
  jacoco
  `java-library`
  `maven-publish`
}

group = "com.github.cedardevs"
version = "master-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":schemas-core"))
  implementation("org.apache.commons:commons-text:1.6")

  testImplementation(project(":schemas-parse"))
  testImplementation("org.spockframework:spock-core:1.1-groovy-2.4")
}

tasks {
  val javadocJar = register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    dependsOn(javadoc)
    from(javadoc.get().destinationDir)
  }
  val testJar = register<Jar>("testJar") {
    archiveClassifier.set("test")
    from(sourceSets.test.get().output)
  }
  build {
    dependsOn(jar, javadocJar, testJar)
  }
}

publishing {
  publications {
    create<MavenPublication>("main") {
      from(components["java"])
      artifact(tasks["javadocJar"])
      artifact(tasks["testJar"])
    }
  }
}