plugins {
    java
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

subprojects {
    apply(plugin="java")
    apply(plugin="maven-publish")

    group = "xyz.mauwh.featherchat"
    version = "1.0-SNAPSHOT"

    java.targetCompatibility = JavaVersion.VERSION_17
    java.sourceCompatibility = JavaVersion.VERSION_17

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.aikar.co/content/groups/aikar/")
    }
}

tasks {
    clean {
        this.delete.add(File("jars"))
    }
}