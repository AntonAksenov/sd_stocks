plugins {
    kotlin("jvm") version "1.8.0"
    application
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.spring.io/release")
    }
    maven {
        url = uri("https://jcenter.bintray.com/")
    }
}

dependencies {
//    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("khttp:khttp:1.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}

tasks.jar {
    manifest.attributes["Main-Class"] = "market.Main"
        manifest.attributes["Class-Path"] = configurations
        .runtimeClasspath
        .get()
        .joinToString(separator = " ") { file ->
            "libs/${file.name}"
        }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
/*
tasks.jar {
    manifest.attributes["Main-Class"] = "market.Main"
//    manifest.attributes["Class-Path"] = configurations
//        .runtimeClasspath
//        .get()
//        .joinToString(separator = " ") { file ->
//            "libs/${file.name}"
//        }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

//val fatJar = task("fatJar", type = Jar::class) {
//    baseName = "${project.name}-fat"
//    manifest {
//        attributes["Implementation-Title"] = "Gradle Jar File Example"
//        attributes["Implementation-Version"] = version
//        attributes["Main-Class"] = "market.MainKt"
//    }
//    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
//    with(tasks.jar.get() as CopySpec)
//}
//
//tasks {
//    "build" {
//        dependsOn(fatJar)
//    }
//}*/