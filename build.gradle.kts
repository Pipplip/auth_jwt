plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kover)

    // Code Quality
    alias(libs.plugins.ktlint)

    // Test Framework
    alias(libs.plugins.kotest)

    // Spring
    id("org.springframework.boot") version
        libs.versions.spring.boot
            .get()
    id("io.spring.dependency-management") version
        libs.versions.spring.dependency.management
            .get()
}

group = "de.phbe"
version = "0.0.1"
description = "auth_jwt"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // SpringBoot
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)

    // Kotlin
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.jackson)

    // JWT
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // DB Migration
    implementation(libs.flyway.core)

    // Database - MySQL
    runtimeOnly(libs.mysql.connector)

    // H2 Database für test, dev environment
    implementation(libs.h2.db)

    // Swagger
    implementation(libs.springdoc.openapi.ui)

    // Test
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.kotest.runner.junit5.jvm)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.extensions.junitxml)
    testImplementation(libs.kotest.extensions.spring)
//    testImplementation(libs.kotest.extensions.core)
    testImplementation(libs.mockk)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.test {
    systemProperty("kotest.framework.config.fqn", "cloud.wowgroup.testengine.KotestProjectConfig")

    finalizedBy(tasks.koverLog, tasks.koverXmlReport)

    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }

    reports {
        junitXml.required = true
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    ignoreFailures.set(true)
}

// configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
//    ignoreFailures.set(false)
//    version.set("1.8.0")
//    filter {
//        exclude { it -> it.file.path.contains("generated") }
//        exclude { it -> it.file.path.contains("${File.separator}build${File.separator}") }
//        exclude("**/build/**")
//        exclude { it -> it.file.path.contains("generate-resources") }
//        exclude("**/generate-resources/**")
//    }
// }

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
