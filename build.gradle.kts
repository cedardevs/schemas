plugins {
  groovy
  jacoco
  id("com.commercehub.gradle.plugin.avro") version "0.9.1"
  `java-library`
  `maven-publish`
}

group = "com.github.cedardevs"
version = "0.1.1"

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

tasks {
  val sourceJar = register<Jar>("sourceJar") {
    classifier = "sources"
    from(sourceSets.main.get().allJava)
  }
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
    dependsOn(sourceJar, javadocJar, testJar)
  }
}

publishing {
  publications {
    create<MavenPublication>("main") {
      from(components["java"])
      artifact(tasks["sourceJar"])
      artifact(tasks["javadocJar"])
      artifact(tasks["testJar"])
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
