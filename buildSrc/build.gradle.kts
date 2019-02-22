import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.21"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("com.github.fge:json-schema-avro:0.1.4")
  implementation("org.apache.avro:avro:1.8.2")
  implementation(gradleApi())
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}
