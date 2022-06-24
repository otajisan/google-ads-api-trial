import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.7.0"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  kotlin("jvm") version "1.6.21"
  kotlin("plugin.spring") version "1.6.21"
  // https://plugins.gradle.org/plugin/com.google.cloud.tools.jib
  id("com.google.cloud.tools.jib") version "3.2.1"
  jacoco
}

group = "io.morningcode"
version = "0.0.1-SNAPSHOT"
val buildNumber by extra("1")

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

//val developmentOnly by configurations.creating
//configurations {
//  runtimeClasspath {
//    extendsFrom(developmentOnly)
//  }
//}

repositories {
  mavenCentral()
}

jib {

  from {
    image = "openjdk:17-jdk-slim-bullseye"
  }

  to {
    image =
        System.getenv("AWS_ACCOUNT_ID") + ".dkr.ecr." + System.getenv("AWS_REGION") + ".amazonaws.com/google-ads-api-trial"
    //credHelper = "ecr-login"
    tags = setOf("$version.${extra["buildNumber"]}")
  }

  container {
    creationTime = "USE_CURRENT_TIMESTAMP"

//    labels = mapOf(
//        "maintainer" to "otajisan <mtaji@morningcode.io>"
//    )
    jvmFlags = listOf(
        "-Xms512m",
        "-Xmx1024m",
        "-Duser.language=ja",
        "-Duser.timezone=Asia/Tokyo",
    )
    environment = mapOf(
        "SPRING_PROFILES_ACTIVE" to "production"
    )
    workingDirectory = "/google-ads-api-trial"
    volumes = listOf("/data")
    ports = listOf(
        "9081",
        "39081"
    )
  }
}

// https://developers.google.com/google-ads/api/docs/client-libs/java/getting-started
val googleAdsSdkVersion = "19.0.0"
// https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-kotlin
val openApiVersion = "1.6.9"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  // Spring Security
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.security:spring-security-oauth2-client")
  implementation("org.springframework.security:spring-security-oauth2-jose")
  // Google Ads SDK
  implementation("com.google.api-ads:google-ads:${googleAdsSdkVersion}")
  // Open API
  implementation("org.springdoc:springdoc-openapi-kotlin:${openApiVersion}")
  implementation("org.springdoc:springdoc-openapi-webmvc-core:${openApiVersion}")
  // Logging
  implementation("net.logstash.logback:logstash-logback-encoder:7.2")

  testImplementation("org.springframework.boot:spring-boot-starter-test")

  // Dev Tool
  developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

// Coverage Report
jacoco {
  toolVersion = "0.8.7"
  reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

tasks.test {
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  reports {
    xml.required.set(false)
    csv.required.set(false)
    html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
  }

  dependsOn(tasks.test)
}
