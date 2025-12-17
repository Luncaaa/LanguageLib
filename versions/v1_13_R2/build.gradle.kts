repositories {
    maven("https://libraries.minecraft.net/")
}

dependencies {
    implementation(project(":versions:common"))
    compileOnly("com.mojang:authlib:1.5.25")
}