plugins {
    id("net.neoforged.moddev") version("2.0.+")
}

repositories {
    google()
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    implementation(project(":")) {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "net.neoforged")
    }
}

neoForge {
    version = "26.1.0.1-beta"

    runs {
        create("client") {
            client()
        }
    }
}

tasks.jar { enabled = false }