import org.apache.tools.ant.taskdefs.Copy
import org.gradle.api.internal.file.copy.CopyAction

plugins {
	java
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
}


group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}

}

repositories {
	mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// RestDocs API Spec 의존성 추가
	testImplementation ("com.epages:restdocs-api-spec:0.18.2")
	implementation("mysql:mysql-connector-java:8.0.28")

	//ascii 관련 의존성
	testImplementation("org.springframework.restdocs:spring-restdocs-asciidoctor")


}


tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
	inputs.dir(project.extra["snippetsDir"]!!)
	outputs.dir("build/asciidoc/html5");  // 원하는 출력 디렉토리
	dependsOn(tasks.test)
}
//확인필요
task("copyDocument"){
	dependsOn("asciidoctor")
	doLast{
		copy {
			from("build/asciidoc/html5") // Asciidoctor 출력 디렉토리에서 파일 복사
			into("src/main/resources/static/docs")
		}
	}
}
//확인필요



