plugins {
    id("com.gradleup.shadow") version("latest.release")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("com.zaxxer:HikariCP:6.2.1")

    implementation(project(":api"))
    implementation(project(":v1_18_R1"))
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        exclude("org/slf4j/**")
        minimize()
        relocate("net.kyori", "shaded.net.kyori")
        relocate("com.zaxxer", "shaded.com.zaxxer")
        archiveFileName.set("${project.parent?.name}-${project.version}.jar")
        destinationDirectory.set(file("../build/libs"))
    }

    assemble {
        dependsOn(shadowJar)
    }
}