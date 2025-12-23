plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
    id("org.jetbrains.compose") version "1.9.3"
    id("maven-publish")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
}

version = "0.1.0"
group = "dev.aperso"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    google()
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.1")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:0.16.14")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.116.7+1.21.1")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.13.8+kotlin.2.3.0")

    val transitiveInclude by configurations.creating

    transitiveInclude(implementation(compose.desktop.windows_x64)!!)
    transitiveInclude(implementation(compose.material3)!!)

    transitiveInclude.resolvedConfiguration.lenientConfiguration.artifacts.forEach {
        val id = it.moduleVersion.id.toString()
        include(id)
    }
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            artifact(tasks["remapJar"]) {
                classifier = ""
            }
        }
    }
    repositories {
        mavenLocal()
    }
}