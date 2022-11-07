plugins {
    java
    `java-library`
    signing
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "xyz.dynxsty"
val archivesBaseName = "dih4jda"
version = "1.6-alpha.1"

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
    maven(url = "https://jitpack.io")
}


dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    implementation("net.dv8tion:JDA:5.0.0-alpha.22")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

val jar: Jar by tasks
val javadoc: Javadoc by tasks
val build: Task by tasks

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val javadocJar = task<Jar>("javadocJar") {
    from("src/main/java")
    dependsOn(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc.destinationDir)
}

//Needed for some reason by maven central
val sourcesJar = task<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from("src/main/java")
}

javadoc.apply {
    options.memberLevel = JavadocMemberLevel.PUBLIC
    options.encoding = "UTF-8"

    (options as? StandardJavadocDocletOptions)?.let { opt ->
        opt.addStringOption("Xdoclint:none", "-quiet")
        opt.addStringOption("charSet", "UTF-8")
    }

    dependsOn(sourcesJar)
    source = sourcesJar.source.asFileTree
    exclude("MANIFEST.MF")
}

build.apply {
    dependsOn(jar)
    dependsOn(javadocJar)
}

fun getProjectProperty(name: String) = project.properties[name] as? String

////////////////////////////////////////
////////////////////////////////////////
////                                ////
////     Publishing And Signing     ////
////                                ////
////////////////////////////////////////
////////////////////////////////////////

buildscript {
    repositories {
        mavenCentral()
    }
}

publishing {
    publications {
        register("Release", MavenPublication::class) {
            from(components["java"])

            artifactId = archivesBaseName
            groupId = group as String
            version = version as String

            artifact(javadocJar)
            artifact(sourcesJar)

            //Creating the pom required for maven central
            pom {
                packaging = "jar"
                name.set(archivesBaseName)
                description.set("A very easy-to-use Interaction Handler for the Java Discord API!")
                url.set("https://github.com/DynxstyGIT/DIH4JDA")

                scm {
                    url.set("https://github.com/DynxstyGIT/DIH4JDA")
                    connection.set("scm:git:git://github.com/DynxstyGIT/DIH4JDA")
                    developerConnection.set("scm:git:ssh:git@github.com:DynxstyGIT/DIH4JDA")
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("Dynxsty")
                        name.set("Jason Dean Lessenich")
                        email.set("jasonlessenich@gmail.com")
                    }

                    developer {
                        id.set("Denux")
                        name.set("Timon Thomas Klinkert")
                        email.set("dev@denux.dev")
                    }
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

// Turn off sign tasks if we don't have a key
val canSign: Boolean = getProjectProperty("signing.keyId") != null
if (canSign) {
    signing {
        useGpgCmd()
        sign(publishing.publications)
        sign(configurations.archives.get())
    }
}

tasks.create("release") {
    // Only close repository after release is published
    val closeSonatypeStagingRepository by tasks
    closeSonatypeStagingRepository.mustRunAfter(tasks.withType<PublishToMavenRepository>())
    dependsOn(tasks.withType<PublishToMavenRepository>())

    // Closes the sonatype repository and publishes to maven central
    val closeAndReleaseSonatypeStagingRepository: Task by tasks
    dependsOn(closeAndReleaseSonatypeStagingRepository)

    // Builds all jars for publications
    dependsOn(build)
}