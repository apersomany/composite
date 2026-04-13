import groovy.json.JsonBuilder

plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
    id("org.jetbrains.compose") version "1.9.3"
    id("maven-publish")
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
}

version = "0.5.0"
group = "dev.aperso"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    google()
    maven("https://maven.neoforged.net/releases/")
}

fun isFatjarGroup(group: String) = group.startsWith("org.jetbrains.compose")
    || group.startsWith("org.jetbrains.skiko")
    || group.startsWith("org.jetbrains.skia")
    || group.startsWith("androidx.")

val natives = arrayListOf<File>()
val includedJars = arrayListOf<ResolvedArtifact>()

dependencies {
    minecraft("com.mojang:minecraft:26.1")

    implementation("net.fabricmc:fabric-loader:0.18.4")
    implementation("net.fabricmc.fabric-api:fabric-api:0.144.0+26.1")
    implementation("net.fabricmc:fabric-language-kotlin:1.13.9+kotlin.2.3.10")

    // https://maven.neoforged.net/#/releases/net/neoforged
    compileOnly("net.neoforged:neoforge:26.1.0.1-beta")
    compileOnly("net.neoforged:bus:8.0.5")
    compileOnly("net.neoforged.fancymodloader:loader:11.0.3")
    compileOnly("net.neoforged.fancymodloader:spi:3.0.9")

    val transitiveInclude by configurations.creating
    transitiveInclude(implementation(compose.material3)!!)
    transitiveInclude(implementation(compose.materialIconsExtended)!!)
    transitiveInclude(implementation(compose.desktop.windows_x64)!!)
    transitiveInclude(implementation(compose.desktop.windows_arm64)!!)
    transitiveInclude(implementation(compose.desktop.macos_x64)!!)
    transitiveInclude(implementation(compose.desktop.macos_arm64)!!)
    transitiveInclude(implementation(compose.desktop.linux_x64)!!)
    transitiveInclude(implementation(compose.desktop.linux_arm64)!!)
    transitiveInclude(implementation("androidx.collection:collection:1.5.0")!!)
    transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
        val group = it.moduleVersion.id.group
        when {
            group == "org.jetbrains.skiko" -> natives.add(it.file)
            isFatjarGroup(group) -> {}
            else -> {
                include(it.moduleVersion.id.toString())
                includedJars.add(it)
            }
        }
    }
}

val generateJarJarMetadata by tasks.registering {
    val outputFile = layout.buildDirectory.file("jarjar/metadata.json")
    outputs.file(outputFile)
    doLast {
        val entries = includedJars.map { art ->
            val id = art.moduleVersion.id
            mapOf(
                "identifier" to mapOf(
                    "group" to id.group,
                    "artifact" to id.name
                ),
                "version" to mapOf(
                    "range" to "[${id.version},)",
                    "artifactVersion" to id.version
                ),
                "path" to "META-INF/jars/${art.file.name}"
            )
        }
        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(JsonBuilder(mapOf("jars" to entries)).toPrettyString())
        }
    }
}

tasks.jar {
    from(generateJarJarMetadata) { into("META-INF/jarjar/") }
    val includeFiles = configurations.getByName("transitiveInclude")
        .resolvedConfiguration.resolvedArtifacts
        .filter { it.file.name.endsWith(".jar") && isFatjarGroup(it.moduleVersion.id.group) }
        .map { it.file }
    from(includeFiles.map { zipTree(it) }) {
        exclude("**/*.dll", "**/*.so", "**/*.dylib")
    }
    from(natives.map { zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register("runClientFabric") {
    group = "composite"
    description = "Runs the client with the Fabric version of Composite"
    dependsOn(tasks.named("runClient"))
}

tasks.register("runClientNeoForge") {
    group = "composite"
    description = "Runs the client with the NeoForge version of Composite"
    dependsOn(project(":run-neoforge").tasks.named("runClient"))
}

tasks.processResources {
    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
        expand("version" to version)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            artifact(tasks["jar"]) {
                classifier = ""
            }
        }
    }
    repositories {
        mavenLocal()
    }
}