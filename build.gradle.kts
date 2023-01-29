import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("nu.studer.jooq") version "7.1.1"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.taehyeon"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
ext["jooq.version"] = "3.16.4"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-configuration-processor:2.7.5")

    // undertow 가 tomcat 에 비해 성능상 이점이 많다는 벤치마크의 결과에 따라 embedded was 로 undertow 사용.
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    modules {
        module("org.springframework.boot:spring-boot-starter-tomcat") {
            replacedBy("org.springframework.boot:spring-boot-starter-undertow", "Use Undertow instead of Tomcat")
        }
    }
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("com.h2database:h2")

    /* DB */
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    //jooq
    jooqGenerator("org.jooq:jooq-meta-extensions")
    jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")

    /* Swagger */
    implementation("io.springfox:springfox-boot-starter:3.0.0")

    /* 저용량의 JSON 처리에 좋은 Gson 사용 */
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")

    /* Test */
    testAnnotationProcessor("org.projectlombok:lombok")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.6")

    /* Spring AOP */
    implementation("org.springframework.boot:spring-boot-starter-aop:2.7.5")

    /* Spring Actuator */
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.7.5")

    /* Spring Validation*/
    implementation("org.springframework.boot:spring-boot-starter-validation:2.7.5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JavaVersion.VERSION_17.toString();
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jooq {
    version.set(dependencyManagement.importedProperties[project.ext["jooq.version"]])
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                        isOutputSchemaToDefault = true
                        properties = listOf(
                            Property()
                                .withKey("scripts")
                                .withValue("src/main/resources/config/db/migration/*.sql"),
                            Property()
                                .withKey("sort")
                                .withValue("flyway"),
                            // DDLDatabase 이용시 H2 를 이용함으로 인해 발생되는 문제 해결을 위해 lowercase 설정 추가
                            Property()
                                .withKey("defaultNameCase")
                                .withValue("lower")
                        )
                    }
                    generate.apply {
                        isDeprecated = false
                        isGeneratedAnnotation = true
                        isRecords = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "com.coway.metrics.sales.repository"
                        directory = "build/generated-src/jooq/main"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}
