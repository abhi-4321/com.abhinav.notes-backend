val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kmongo_version: String by project
val commons_codec_version: String by project

plugins {
    application
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.8"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

group = "com.abhinav"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

val sshAntTask = configurations.create("sshAntTask")

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    implementation("org.litote.kmongo:kmongo:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongo_version")

    implementation("commons-codec:commons-codec:$commons_codec_version")

    sshAntTask("org.apache.ant:ant-jsch:1.10.12")
}

tasks {
    create("stage").dependsOn("installDist")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get()
        )
    }
}

ant.withGroovyBuilder {
    "taskdef"(
        "name" to "scp",
        "classname" to "org.apache.tools.ant.taskdefs.optional.ssh.Scp",
        "classpath" to configurations.get("sshAntTask").asPath
    )
    "taskdef"(
        "name" to "ssh",
        "classname" to "org.apache.tools.ant.taskdefs.optional.ssh.SSHExec",
        "classpath" to configurations.get("sshAntTask").asPath
    )
}

//task("deploy") {
//    dependsOn("clean", "shadowJar")
//    ant.withGroovyBuilder {
//        doLast {
//            val knownHosts = File.createTempFile("knownhosts", "txt")
//            val user = "root"
//            val host = "145.14.158.77"
//            val key = file("keys/jwtauthkey-yt")
//            val jarFileName = "com.plcoding.ktor-jwt-auth-$version-all.jar"
//            try {
//                "scp"(
//                    "file" to file("build/libs/$jarFileName"),
//                    "todir" to "$user@$host:/root/jwtauth",
//                    "keyfile" to key,
//                    "trust" to true,
//                    "knownhosts" to knownHosts
//                )
//                "ssh"(
//                    "host" to host,
//                    "username" to user,
//                    "keyfile" to key,
//                    "trust" to true,
//                    "knownhosts" to knownHosts,
//                    "command" to "mv /root/jwtauth/$jarFileName /root/jwtauth/jwtauth.jar"
//                )
//                "ssh"(
//                    "host" to host,
//                    "username" to user,
//                    "keyfile" to key,
//                    "trust" to true,
//                    "knownhosts" to knownHosts,
//                    "command" to "systemctl stop jwtauth"
//                )
//                "ssh"(
//                    "host" to host,
//                    "username" to user,
//                    "keyfile" to key,
//                    "trust" to true,
//                    "knownhosts" to knownHosts,
//                    "command" to "systemctl start jwtauth"
//                )
//            } finally {
//                knownHosts.delete()
//            }
//        }
//    }
//}