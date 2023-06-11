import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.4"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.jmailen.kotlinter") version "3.12.0"
	id("com.adarshr.test-logger") version "3.2.0"
	kotlin("jvm") version "1.8.10"
	kotlin("plugin.spring") version "1.8.10"
	jacoco
}

group = "com.odenizturker"
java.sourceCompatibility = JavaVersion.VERSION_17

extra["springCloudVersion"] = "2022.0.1"

repositories {
	mavenCentral()
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// Kotlin
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	// Flyway Migration
	implementation("org.flywaydb:flyway-core")

	// Postgres
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.postgresql:r2dbc-postgresql")

	// Internal Libraries
	implementation("com.odenizturker:r2dbc:0.0.4")

	// Swagger support
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.0.4")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
	testImplementation("io.projectreactor:reactor-test")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
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

tasks.bootRun {
	doFirst {
		systemProperty("database.endpoint", System.getenv("DATABASE_ENDPOINT") ?: "localhost:5433")
		systemProperty("database.migration.endpoint", System.getenv("DATABASE_MIGRATION_ENDPOINT") ?: "localhost:5433")
		systemProperty("database.name", System.getenv("DATABASE_NAME") ?: "gather_test")
		systemProperty("database.user", System.getenv("DATABASE_USER") ?: "db_user")
		systemProperty("database.password", System.getenv("DATABASE_PASSWORD") ?: "db_pass")
	}
}


tasks.test {
	doFirst {
		systemProperty("database.endpoint", System.getenv("DATABASE_ENDPOINT") ?: "localhost:5433")
		systemProperty("database.migration.endpoint", System.getenv("DATABASE_MIGRATION_ENDPOINT") ?: "localhost:5433")
		systemProperty("database.name", System.getenv("DATABASE_NAME") ?: "gather_test_test")
		systemProperty("database.user", System.getenv("DATABASE_USER") ?: "db_user")
		systemProperty("database.password", System.getenv("DATABASE_PASSWORD") ?: "db_pass")
	}
	finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = "0.4".toBigDecimal()
			}
		}
	}
}
