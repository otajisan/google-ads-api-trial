import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.3.RELEASE"
	//id("org.springframework.boot") version "2.3.4.RELEASE"
	//id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	id("com.google.cloud.tools.jib") version "3.2.1"
	jacoco
}

group = "io.morningcode"
version = "0.0.1-SNAPSHOT"
java {
//	sourceCompatibility = JavaVersion.VERSION_17
//	targetCompatibility = JavaVersion.VERSION_17

	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "Hoxton.SR8"
//extra["springCloudVersion"] = "2021.0.3"



dependencies {
//	implementation("org.springframework.boot:spring-boot-starter")
//	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.security:spring-security-oauth2-jose")

	implementation("org.springframework.cloud:spring-cloud-starter-config")
	implementation("org.springframework.cloud:spring-cloud-starter-gateway")
	implementation("org.springframework.cloud:spring-cloud-starter-security")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		//jvmTarget = "17"
		jvmTarget = "15"
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
