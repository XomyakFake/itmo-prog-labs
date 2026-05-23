plugins {
    java
    application
}

group = "ru.itmo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("ru.itmo.client.Main")
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.1")
    
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.1")
    
    implementation("com.auth0:java-jwt:4.4.0")
    
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    implementation("net.sf.opencsv:opencsv:2.3")
    
    testImplementation("junit:junit:4.13.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}


tasks.register<Jar>("serverJar") {
    archiveClassifier.set("server")
    manifest {
        attributes["Main-Class"] = "ru.itmo.server.Main"
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Jar>("clientJar") {
    archiveClassifier.set("client")
    manifest {
        attributes["Main-Class"] = "ru.itmo.client.Main"
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile> {
    options.isFork = true
    val heliosJavac = file("/usr/local/openjdk21/bin/javac")
    
    if (heliosJavac.exists()) {
        options.forkOptions.executable = heliosJavac.absolutePath
    } else {

    }
}