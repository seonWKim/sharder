plugins {
    id("java")
    java
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.springframework.boot") version "3.2.2"

    signing
    id("maven-publish")
}

allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

subprojects {
    plugins.apply("java")
    plugins.apply("io.spring.dependency-management")
    plugins.apply("org.springframework.boot")

    dependencyManagement {
        imports {
            mavenBom("com.linecorp.armeria:armeria-bom:1.28.1")
        }
    }

    dependencies {
        implementation("com.linecorp.armeria:armeria-spring-boot3-starter")
        implementation("com.linecorp.armeria:armeria-logback")
        implementation("org.springframework.data:spring-data-jdbc:3.2.5")
        implementation("com.zaxxer:HikariCP:4.0.3")
        implementation("mysql:mysql-connector-java:8.0.33")

        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.2.2")
        annotationProcessor("org.projectlombok:lombok:1.18.32")

        runtimeOnly("com.linecorp.armeria:armeria-spring-boot3-actuator-starter")
        compileOnly("org.projectlombok:lombok:1.18.32")

        testImplementation("org.mockito:mockito-core:5.11.0")
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.2")
        testCompileOnly("org.projectlombok:lombok:1.18.32")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
    }
}
