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

fun getProjectProperty(name: String) = project.properties[name] as? String

group = "xyz.dynxsty"
val archivesBaseName = "dih4jda"
version = "1.6.0-alpha.2"

val javaVersion = JavaVersion.current()
var isCI: Boolean = System.getProperty("GIT_COMMIT") != null // jitpack
        || System.getenv("GIT_COMMIT") != null
        || System.getProperty("GITHUB_ACTIONS") != null // GitHub Actions
        || System.getenv("GITHUB_ACTIONS") != null

if (getProjectProperty("manualCI") != null) {
    isCI = getProjectProperty("manualCI").toBoolean()
}

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
    maven(url = "https://jitpack.io")
}


dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("ch.qos.logback:logback-classic:1.4.4")

    implementation("com.github.DV8FromTheWorld:JDA:f181320b10")
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

/*
To find all missing javadocs / all javadocs warnings execute this command:
./gradlew javadocJar --warning-mode all --stacktrace
 */
javadoc.apply {
    isFailOnError = isCI

    //Javadocs are scuffed on these version I don't know and care why.
    if (javaVersion <= JavaVersion.VERSION_13) {
        isFailOnError = false;
    }

    options.memberLevel = JavadocMemberLevel.PUBLIC
    options.encoding = "UTF-8"

    (options as? StandardJavadocDocletOptions)?.let { opt ->
        opt.addStringOption("charSet", "UTF-8")

        // Fix for https://stackoverflow.com/questions/52326318/maven-javadoc-search-redirects-to-undefined-url
        if (javaVersion in JavaVersion.VERSION_11..JavaVersion.VERSION_12) {
            opt.addBooleanOption("-no-module-directories", true)
        }

        // Java 13 changed accessibility rules.
        // On versions less than Java 13, we simply ignore the errors.
        if (javaVersion >= JavaVersion.VERSION_13) {
            opt.addBooleanOption("Xdoclint:all", true)
        } else {
            opt.addBooleanOption("Xdoclint:all, -accessibility", true)
        }
    }

    dependsOn(sourcesJar)
    source = sourcesJar.source.asFileTree
    exclude("MANIFEST.MF")
}

build.apply {
    dependsOn(jar)
    dependsOn(sourcesJar)
    dependsOn(javadocJar)
}

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
                description.set("A fairly easy-to-use command framework for the Java Discord API, with support for " +
                        "Slash Commands and Context Commands")
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