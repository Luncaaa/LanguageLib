plugins {
    id("com.gradleup.shadow")
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.12.2")
    implementation("com.zaxxer:HikariCP:7.0.2")

    implementation(project(":api"))
    implementation(project(":versions"))
    implementation(project(":versions:common"))
    implementation(project(":versions:v1_13_R2"))
    implementation(project(":versions:v1_18_R1"))
}

tasks {
    shadowJar {
        exclude("org/slf4j/**")
        minimize()
        relocate("net.kyori", "shaded.net.kyori")
        relocate("com.zaxxer", "shaded.com.zaxxer")
        archiveFileName.set("${project.parent?.name}-${project.version}.jar")
        destinationDirectory.set(file("../build/libs"))

        manifest {
            attributes(
                mapOf(
                    "paperweight-mappings-namespace" to "mojang"
                )
            )
        }
    }

    assemble {
        dependsOn(shadowJar)
    }
}