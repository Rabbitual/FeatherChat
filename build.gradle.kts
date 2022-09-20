import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    apply{
        id("xyz.mauwh.java-conventions")
        id("com.github.johnrengelman.shadow") version("7.1.2")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Api"))
    implementation(project(":Bukkit"))
    implementation(project(":FeatherChat"))
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    named<ShadowJar>("shadowJar") {
        relocate("co.aikar", "xyz.mauwh.featherchat.libs.acf")
        relocate("net.kyori", "xyz.mauwh.featherchat.libs.kyori")
        relocate("org.yaml.snakeyaml", "xyz.mauwh.featherchat.libs.snakeyaml")
        archiveBaseName.set("FeatherChat")
        archiveClassifier.set("")
    }
}