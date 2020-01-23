plugins {
  groovy
  jacoco
  id("com.commercehub.gradle.plugin.avro").version("0.9.1")
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
  maven("https://www.jitpack.io")
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  api("org.apache.avro:avro:1.8.2")

  testImplementation("org.codehaus.groovy:groovy:2.4.13")
  testImplementation("org.spockframework:spock-core:1.1-groovy-2.4")
  testImplementation("org.apache.kafka:kafka-streams-test-utils:2.3.0")
  testImplementation("org.apache.kafka:kafka-clients:2.3.0")
  testImplementation("org.apache.kafka:kafka-clients:2.3.0:test")
  testImplementation("org.apache.kafka:kafka_2.12:2.3.0")
  testImplementation("org.apache.kafka:kafka_2.12:2.3.0:test")
  testImplementation("io.confluent:kafka-schema-registry:5.3.0")
  testImplementation("io.confluent:kafka-schema-registry:5.3.0:tests")
  testImplementation("io.confluent:kafka-streams-avro-serde:5.3.0")
  testImplementation("com.github.everit-org.json-schema:org.everit.json.schema:1.9.2")
  testImplementation("org.json:json:20180813")
}

tasks {
  generateAvroJava {
    source("src/main/resources/avro")
    setOutputDir(file("$buildDir/generated/java"))
  }
  val generateJsonSchema = register<GenerateJsonSchema>("generateJsonSchema") {
    from("src/main/resources/avro")
    into("$buildDir/generated/resources/json")
  }
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
  processResources {
    dependsOn(generateJsonSchema)
  }
  build {
    dependsOn(jar, sourceJar, javadocJar, testJar)
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
    resources {
      srcDir("$buildDir/generated/resources")
    }
  }
}

tasks.assemble {
  dependsOn(tasks.named<Jar>("sourceJar"))
}
