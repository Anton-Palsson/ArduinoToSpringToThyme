plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fazecast:jSerialComm:2.9.3")  // jSerialComm beroende
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // MySQL Connector - ändra från runtimeOnly till implementation
    implementation("mysql:mysql-connector-java:8.0.30")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}



tasks.withType<Test> {
    useJUnitPlatform()
}
