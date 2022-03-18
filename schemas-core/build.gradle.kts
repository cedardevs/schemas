plugins {
  groovy
  jacoco
  id("com.commercehub.gradle.plugin.avro").version("0.99.99")
  `java-library`
  `maven-publish`
}

group = "com.github.cedardevs"
version = "master-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  mavenCentral()
  maven("https://www.jitpack.io")
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  api("org.apache.avro:avro:1.8.2")

  testImplementation("org.slf4j:slf4j-simple:${Versions.SLF4J}")
  testImplementation("org.codehaus.groovy:groovy:${Versions.GROOVY}")
  testImplementation("org.spockframework:spock-core:${Versions.SPOCK}")
  testImplementation("org.apache.kafka:kafka-streams-test-utils:${Versions.KAFKA}")
  testImplementation("org.apache.kafka:kafka-clients:${Versions.KAFKA}")
  testImplementation("org.apache.kafka:kafka-clients:${Versions.KAFKA}:test")
  testImplementation("org.apache.kafka:kafka_2.12:${Versions.KAFKA}")
  testImplementation("org.apache.kafka:kafka_2.12:${Versions.KAFKA}:test")
  testImplementation("io.confluent:kafka-schema-registry:${Versions.CONFLUENT}")
  testImplementation("io.confluent:kafka-schema-registry:${Versions.CONFLUENT}:tests")
  testImplementation("io.confluent:kafka-streams-avro-serde:${Versions.CONFLUENT}")
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
