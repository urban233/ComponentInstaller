import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.text.SimpleDateFormat
import java.util.*

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.8.0"
}

group = "org.ibci"
version = "0.0.3"

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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("net.java.dev.jna:jna:5.8.0")
    implementation("net.java.dev.jna:jna-platform:5.8.0")
    implementation("org.zeromq:jeromq:0.6.0")
}

compose.desktop {
    application {
        mainClass = "org.ibci.componentinstaller.main.MainKt"

        nativeDistributions {
            //targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            targetFormats(TargetFormat.Exe)  // To build the actual exe file use the gradle task createDistributable!
            packageName = "ComponentInstaller"
            packageVersion = version.toString()
        }
    }
}

tasks.register("updateVersionHistory") {
    doLast {
        val versionHistoryFile = file("src/main/resources/version_history.json")
        val newVersion = project.version.toString()
        val newReleaseDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

        if (!versionHistoryFile.exists()) {
            throw GradleException("version_history.json file does not exist!")
        }

        // Read and parse the JSON file manually
        val versionHistoryContent = versionHistoryFile.readText()
        val versionHistory = parseJson(versionHistoryContent)

        val newEntry = mapOf("version" to newVersion, "releaseDate" to newReleaseDate)
        val updatedVersionHistory = versionHistory.toMutableList().apply { add(newEntry) }

        val updatedJson = toJsonString(updatedVersionHistory)
        versionHistoryFile.writeText(updatedJson)

        println("Version history updated with version: $newVersion and release date: $newReleaseDate")
    }
}

fun parseJson(json: String): List<Map<String, String>> {
    // A very simple JSON parser tailored to the expected format of version_history.json
    val regex = Regex("""\{"version":\s*"(.+?)",\s*"releaseDate":\s*"(.+?)"}""")
    return regex.findAll(json).map { matchResult ->
        mapOf("version" to matchResult.groupValues[1], "releaseDate" to matchResult.groupValues[2])
    }.toList()
}

fun toJsonString(versionHistory: List<Map<String, String>>): String {
    val entries = versionHistory.joinToString(",\n") { entry ->
        """    {"version": "${entry["version"]}", "releaseDate": "${entry["releaseDate"]}"}"""
    }
    return """{
  "versionHistory": [
$entries
  ]
}"""
}

tasks.register("generateInnoSetupScript") {
    doLast {
        val template = file("src/main/resources/inno_setup/base.iss").readText()
        val processedScript = template.replace("{#AppVersion}", version.toString())
        file("src/main/resources/inno_setup/base_with_version.iss").writeText(processedScript)
    }
}

tasks.register<Exec>("packageWithInnoSetup") {
    dependsOn("updateVersionHistory", "generateInnoSetupScript", "createDistributable")
    val innoSetupPath = "C:\\Program Files (x86)\\Inno Setup 6\\ISCC.exe"
    val setupScript = file("src/main/resources/inno_setup/base_with_version.iss")
    commandLine(innoSetupPath, setupScript.absolutePath)
}