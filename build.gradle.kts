import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "org.ibci"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
}

compose.desktop {
    application {
        mainClass = "org.ibci.componentinstaller.main.MainKt"

        nativeDistributions {
            //targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            targetFormats(TargetFormat.Exe)  // To build the actual exe file use the gradle task createDistributable!
            packageName = "ComponentInstaller"
            packageVersion = "0.0.1"
        }
    }
}
