import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(rootProject)
    implementation(libs.minestom)
}

tasks {
    application {
        mainClass.set("code.frfole.pocs.demo.Main")
    }

    withType<ShadowJar> {
        archiveFileName.set("pocs4mc-demo.jar")
    }
}
