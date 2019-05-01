plugins {
  groovy
  jacoco
  `java-library`
  `maven-publish`
}

group = "com.github.cedardevs"
version = "master-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://jitpack.io")
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  implementation(project(":schemas-core"))
  implementation("org.apache.commons:commons-text:1.6")
  implementation("org.codehaus.groovy:groovy-xml:2.4.16")
  implementation("org.codehaus.groovy:groovy-json:2.4.16")

  testImplementation("org.spockframework:spock-core:1.1-groovy-2.4")
}

tasks {
  val javadocJar = register<Jar>("javadocJar") {
    classifier = "javadoc"
    dependsOn(javadoc)
    from(javadoc.get().destinationDir)
  }
  val testJar = register<Jar>("testJar") {
    classifier = "test"
    from(sourceSets.test.get().output)
  }
  build {
    dependsOn(jar, javadocJar/*, testJar*/)
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
