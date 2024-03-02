plugins {
    java
}

group = "dev.flero"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        val javaVersion = JavaVersion.VERSION_17
        targetCompatibility = javaVersion.toString()
        sourceCompatibility = javaVersion.toString()
    }
}
