plugins {
    id("java")
}

group = "code.frfole"
version = "1.0-SNAPSHOT"


allprojects {
    apply(plugin = "java")

    group = "code.frfole"
    version = rootProject.version

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    tasks.test {
        useJUnitPlatform()
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(libs.minestom)
}
