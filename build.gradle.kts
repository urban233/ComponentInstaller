import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.com.google.gson.*
import java.io.ByteArrayOutputStream
import java.time.LocalDate

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.8.0"
}

group = "org.ibci"
version = "0.1.1"

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



// Here are custom gradle tasks

val versionHistoryFile = file("src/main/resources/version_history.json")

/**
 * Appends the latest version number to the version_history.json file
 */
tasks.register<DefaultTask>("appendVersion") {
    description = "Appends current project version to versionHistory.json"

    doLast {
        val currentVersion = project.version.toString()
        val releaseDate = LocalDate.now().toString()

        val existingData = if (versionHistoryFile.exists()) {
            // Read existing data if file exists, handle potential exceptions
            try {
                Gson().fromJson(versionHistoryFile.readText(), JsonObject::class.java)
            } catch (e: Exception) {
                logger.warn("Error reading existing version history: $e")
                JsonObject()
            }
        } else {
            JsonObject()
        }

        val versionHistory = existingData.getAsJsonArray("versionHistory") ?: JsonArray()
        val newEntry = JsonObject().apply {
            addProperty("version", currentVersion)
            addProperty("releaseDate", releaseDate)
        }
        versionHistory.add(newEntry)
        val tmpGson: Gson = GsonBuilder().setPrettyPrinting().create()
        versionHistoryFile.writeText(tmpGson.toJson(existingData))
    }
}

/**
 * Substitutes the version number placeholder in the inno setup script with the latest one
 */
tasks.register("updateVersionInInnoSetupScriptOnline") {
    doLast {
        val template = file("$projectDir/deployment/inno_setup/base.iss").readText()
        val processedScript = template.replace("{#AppVersion}", version.toString())
        file("$projectDir/deployment/inno_setup/base_with_version.iss").writeText(processedScript)
    }
}

/**
 * Substitutes the version number placeholder in the inno setup script with the latest one
 */
tasks.register("updateVersionInInnoSetupScriptOffline") {
    doLast {
        val template = file("$projectDir/deployment/inno_setup/baseOffline.iss").readText()
        val processedScript = template.replace("{#AppVersion}", version.toString())
        file("$projectDir/deployment/inno_setup/base_offline_with_version.iss").writeText(processedScript)
    }
}

tasks.register<Exec>("prepareInnoSetupScriptOnline") {
    mustRunAfter("createDistributable", "publishWindowsCmdElevator")
    val copyScript = file("$projectDir/deployment/inno_setup/copy_inno_src.bat")
    commandLine("cmd", "/c", copyScript)
}

tasks.register<Exec>("prepareInnoSetupScriptOffline") {
    mustRunAfter("createDistributable", "publishWindowsCmdElevator")
    val copyScript = file("$projectDir/deployment/inno_setup/copy_inno_src_offline.bat")
    commandLine("cmd", "/c", copyScript)
}

tasks.register<Exec>("compileOnlineInnoSetup") {
    dependsOn(
        "appendVersion",
        "updateVersionInInnoSetupScriptOnline",
        "createDistributable",
        "publishWindowsCmdElevator",
        "publishWindowsTasks",
        "prepareInnoSetupScriptOnline"
    )
    mustRunAfter("prepareInnoSetupScriptOnline")
    val innoSetupPath = "C:\\Program Files (x86)\\Inno Setup 6\\ISCC.exe"
    val setupScript = file("$projectDir/deployment/inno_setup/base_with_version.iss")
    commandLine(innoSetupPath, setupScript)
}

tasks.register<Exec>("compileOfflineInnoSetup") {
    dependsOn(
        "appendVersion",
        "updateVersionInInnoSetupScriptOffline",
        "createDistributable",
        "publishWindowsCmdElevator",
        "publishWindowsTasks",
        "prepareInnoSetupScriptOffline"
    )
    mustRunAfter("prepareInnoSetupScriptOffline")
    val innoSetupPath = "C:\\Program Files (x86)\\Inno Setup 6\\ISCC.exe"
    val setupScript = file("$projectDir/deployment/inno_setup/base_offline_with_version.iss")
    commandLine(innoSetupPath, setupScript)
}

tasks.register<Exec>("publishWindowsCmdElevator") {
    // Define the command to publish the project
    val pubxmlFile = file("$projectDir/WindowsWrapper/WindowsCmdElevator/Properties/PublishProfiles/FolderProfile.pubxml")
    commandLine("dotnet", "publish", "$projectDir/WindowsWrapper/WindowsCmdElevator", "/p:PublishProfile=$pubxmlFile")

    // Setting the working directory
    workingDir = file(projectDir)

    // Capture and log the output
    val outputStream = ByteArrayOutputStream()
    standardOutput = outputStream
    errorOutput = outputStream
    isIgnoreExitValue = true

    doLast {
        println(outputStream.toString())
    }
}

tasks.register<Exec>("publishWindowsTasks") {
    // Define the command to publish the project
    val pubxmlFile = file("$projectDir/WindowsWrapper/WindowsTasks/Properties/PublishProfiles/FolderProfile.pubxml")
    commandLine("dotnet", "publish", "$projectDir/WindowsWrapper/WindowsTasks", "/p:PublishProfile=$pubxmlFile")

    // Setting the working directory
    workingDir = file(projectDir)

    // Capture and log the output
    val outputStream = ByteArrayOutputStream()
    standardOutput = outputStream
    errorOutput = outputStream
    isIgnoreExitValue = true

    doLast {
        println(outputStream.toString())
    }
}

//
//tasks.register<Exec>("compileOnlineInnoSetup") {
////    dependsOn(
////        "updateVersionHistory",
////        "generateInnoSetupScript",
////        //"publishWindowsCmdElevator",
////        //"publishWindowsTasks",
////        //"createDistributable"
////    )
//
//    doLast {
//        val copyScript = file("src/main/resources/scripts/copy_inno_src.bat")
//        //commandLine("cmd", copyScript.absolutePath)
//        val innoSetupPath = "C:\\Program Files (x86)\\Inno Setup 6\\ISCC.exe"
//        val setupScript = file("src/main/resources/inno_setup/base_with_version.iss")
//        commandLine(innoSetupPath, setupScript.absolutePath)
//    }
//
////    val copyScript = file("src/main/resources/scripts/copy_inno_src.bat")
////    commandLine("cmd.exe", copyScript.absolutePath)
////    val innoSetupPath = "C:\\Program Files (x86)\\Inno Setup 6\\ISCC.exe"
////    val setupScript = file("src/main/resources/inno_setup/base_with_version.iss")
////    commandLine(innoSetupPath, setupScript.absolutePath)
//}
//
//// Function to configure the publishing task
//fun configurePublishTask(taskName: String, projectDir: String) {
//    tasks.register<Exec>(taskName) {
//        // Define the command to publish the project
//        val pubxmlFile = file("$projectDir/Properties/PublishProfiles/FolderProfile.pubxml")
//        commandLine("dotnet", "publish", projectDir, "/p:PublishProfile=$pubxmlFile")
//
//        // Setting the working directory
//        workingDir = file(projectDir)
//
//        // Capture and log the output
//        val outputStream = ByteArrayOutputStream()
//        standardOutput = outputStream
//        errorOutput = outputStream
//        isIgnoreExitValue = true
//
//        doLast {
//            println(outputStream.toString())
//        }
//    }
//}
//
//configurePublishTask("publishWindowsCmdElevator", "$projectDir/WindowsWrapper/WindowsCmdElevator")
//configurePublishTask("publishWindowsTasks", "$projectDir/WindowsWrapper/WindowsTasks")
//
//
//tasks.register("generateOfflineInnoSetupScript") {
//    doLast {
//        val template = file("src/main/resources/inno_setup/baseOffline.iss").readText()
//        val processedScript = template.replace("{#AppVersion}", version.toString())
//        file("src/main/resources/inno_setup/base_offline_with_version.iss").writeText(processedScript)
//    }
//}
//
//tasks.register<Exec>("packageWithInnoSetupForOffline") {
//    // IMPORTANT: This task does NOT update the version number of the installer, to do this please use the task packageWithInnoSetup!
//    dependsOn("generateOfflineInnoSetupScript", "publishWindowsCmdElevator", "publishWindowsTasks", "createDistributable")
//    val innoSetupPath = "C:\\Program Files (x86)\\Inno Setup 6\\ISCC.exe"
//    val setupScript = file("src/main/resources/inno_setup/base_offline_with_version.iss")
//    commandLine(innoSetupPath, setupScript.absolutePath)
//}
