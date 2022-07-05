buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "com.dynxsty"
val archivesBaseName = "dih4jda"
version = "1.6"

publishing {
    publications {
        register("Release", MavenPublication::class) {
            from(components["java"])

            artifactId = archivesBaseName
            groupId = group as String
            version = version as String
        }
    }
}

repositories {
    mavenCentral()
}

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
    maven(url = "https://jitpack.io")
}


dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.13")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    implementation("org.reflections:reflections:0.10.2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<Test> { useJUnitPlatform() }