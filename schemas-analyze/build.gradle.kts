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
  implementation("org.apache.commons:commons-text:1.8")
  implementation("org.locationtech.jts.io:jts-io-common:1.16.1")
  implementation("org.jsoup:jsoup:1.12.1")
  implementation("org.slf4j:slf4j-simple:1.7.30")
  implementation("com.ibm.icu:icu4j:65.1")

  testImplementation(project(":schemas-parse"))
  testImplementation("org.spockframework:spock-core:1.1-groovy-2.4")
}

tasks {
  val sourceJar = register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allJava)
  }
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
    dependsOn(jar, sourceJar, javadocJar, testJar)
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
