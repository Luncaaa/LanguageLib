plugins {
    id("java")
}

allprojects {
    apply(plugin = "java")
    group = "me.lucaaa"
    version = "1.0"

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    tasks {
        compileJava {
            options.release = 8
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
        implementation("net.kyori:adventure-api:4.19.0")
        implementation("net.kyori:adventure-text-minimessage:4.19.0")
        implementation("net.kyori:adventure-text-serializer-legacy:4.19.0")
        implementation("net.kyori:adventure-platform-bungeecord:4.3.4")
    }
}