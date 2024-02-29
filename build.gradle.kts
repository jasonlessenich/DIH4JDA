import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    signing
    `java-library`
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.ltgt.errorprone") version "3.1.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

fun getProjectProperty(name: String) = project.properties[name] as? String

group = "xyz.dynxsty"
val archivesBaseName = "dih4jda"
version = "1.6.2"

val javaVersion: JavaVersion = JavaVersion.current()
var isCI: Boolean = System.getProperty("GIT_COMMIT") != null // jitpack
        || System.getenv("GIT_COMMIT") != null
        || System.getProperty("GITHUB_ACTIONS") != null // GitHub Actions
        || System.getenv("GITHUB_ACTIONS") != null

/*
Add the manualCI property to your gradle.properties
Sets failOnError on the javadocJar tasks to your specified boolean.
*/
if (getProjectProperty("manualCI") != null) {
    isCI = getProjectProperty("manualCI").toBoolean()
}

configure<SourceSetContainer> {
    register("examples") {
        java.srcDir("src/examples/java")
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
}

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
    maven(url = "https://jitpack.io")
}

val lombokVersion = "1.18.30"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("ch.qos.logback:logback-classic:1.5.1")

    api("net.dv8tion:JDA:5.0.0-beta.20") {
        exclude(module = "opus-java")
    }

    //code saftey
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    errorprone("com.google.errorprone:error_prone_core:2.25.0")

    //Lombok's annotations
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    //Sets the dependencies for the examples
    configurations["examplesImplementation"].withDependencies {
        addAll(configurations["implementation"].allDependencies)
        addAll(configurations["compileOnly"].allDependencies)
    }
}

val jar: Jar by tasks
val shadowJar: ShadowJar by tasks
val javadoc: Javadoc by tasks
val build: Task by tasks

shadowJar.archiveClassifier.set("withDependencies")

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    //enable this when the StaticAssignmentInConstructor error-prone error is fixed
    //if (isCI) options.compilerArgs.add("-Werror")

    //error-prone configuration
    options.errorprone.disableWarningsInGeneratedCode.set(true)
    options.errorprone.errorproneArgs.addAll(
            "-Xep:AnnotateFormatMethod:OFF", "-Xep:FutureReturnValueIgnored:OFF", "-Xep:NotJavadoc:OFF")
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

    //Fixes the error where the executor could not be found and the build fails.
    if (javaVersion <= JavaVersion.VERSION_20) {
        isFailOnError = false
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
            //Can be ignored because JavaDocs are generated using Java 21.
            opt.addBooleanOption("Xdoclint:all,-missing,-accessibility", true)
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
    dependsOn(shadowJar)
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
                        email.set("timon.klinkert@denux.dev")
                    }
                }
            }
        }
    }
}

nexusPublishing.repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
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
