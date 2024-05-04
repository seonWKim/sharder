plugins {
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.springframework.boot") version "3.2.2"
}

dependencies {
    implementation(project(":core"))

    implementation("com.linecorp.armeria:armeria-spring-boot3-starter")
    implementation("com.linecorp.armeria:armeria-logback")
    implementation("org.springframework.data:spring-data-jdbc")
    implementation("com.zaxxer:HikariCP")

    implementation("mysql:mysql-connector-java")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    runtimeOnly("com.linecorp.armeria:armeria-spring-boot3-actuator-starter")
    compileOnly("org.projectlombok:lombok")

    testImplementation("org.mockito:mockito-core")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.register<JavaExec>("runJar") {
    dependsOn("jar")
    mainClass.set("com.seonWKim.sharder.SharderApplicationExample")
    classpath = sourceSets["main"].runtimeClasspath
    val profile = project.findProperty("profile") as String? ?: "local"
    args = listOf("--spring.profiles.active=$profile")
}
