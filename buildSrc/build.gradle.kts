import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.20"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("com.github.fge:json-schema-avro:0.1.4")
  api("com.fasterxml.jackson.core:jackson-databind:2.13.4.2")
  api("org.apache.avro:avro:1.11.1")
  implementation(gradleApi())
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}
