plugins {
  groovy
  jacoco
  id("com.commercehub.gradle.plugin.avro") version "0.9.1"
  `java-library`
  `maven-publish`
}

group = "com.github.cedardevs"

repositories {
  mavenCentral()
  maven("https://jitpack.io")
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  api("org.apache.avro:avro:1.8.2")

  testImplementation("org.codehaus.groovy:groovy:2.4.13")
  testImplementation("org.spockframework:spock-core:1.1-groovy-2.4")
  testImplementation("org.apache.kafka:kafka-streams-test-utils:2.0.1")
  testImplementation("org.apache.kafka:kafka-clients:2.0.1")
  testImplementation("org.apache.kafka:kafka-clients:2.0.1:test")
  testImplementation("org.apache.kafka:kafka_2.12:2.0.1")
  testImplementation("org.apache.kafka:kafka_2.12:2.0.1:test")
  testImplementation("io.confluent:kafka-schema-registry:5.0.1")
  testImplementation("io.confluent:kafka-schema-registry:5.0.1:tests")
  testImplementation("io.confluent:kafka-streams-avro-serde:5.0.1")
  testImplementation("com.github.everit-org.json-schema:org.everit.json.schema:1.9.2")
  testImplementation("org.json:json:20180813")
}

tasks.jar {
  baseName = "${rootProject.name}-${project.name}"
}

tasks.register<Jar>("sourceJar") {
  classifier = "sources"
  baseName = "${rootProject.name}-${project.name}"
  from("$projectDir/src")
}

tasks.register<Jar>("testArtifactsJar") {
  classifier = "test"
  baseName = "${rootProject.name}-${project.name}"
  from(sourceSets.test.get().output)
}

publishing {
  publications {
    create<MavenPublication>("main") {
      artifact(tasks["jar"])
      artifact(tasks["sourceJar"])
    }
    create<MavenPublication>("test") {
      artifact(tasks["testArtifactsJar"])
    }
  }
}

avro {
  fieldVisibility = "PRIVATE"
  setCreateSetters("false")
}

tasks.generateAvroJava {
  source("src/main/resources/avro")
  setOutputDir(file("$buildDir/generated/java"))
}

sourceSets {
  main {
    java {
      srcDir(tasks.generateAvroJava.get().outputs)
    }
  }
}

tasks.assemble {
  dependsOn(tasks.named<Jar>("sourceJar"))
}
