import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
    kotlin("jvm") version "1.2.60"
    kotlin("kapt") version "1.2.60"
}

apply(from = rootProject.file("gradle/generated-kotlin-sources.gradle"))

group = "eu.enhan"
version = "1.0-SNAPSHOT"

val arrowVersion = "0.7.3"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))


    compile("io.arrow-kt:arrow-core:$arrowVersion")
    compile("io.arrow-kt:arrow-syntax:$arrowVersion")
    compile("io.arrow-kt:arrow-typeclasses:$arrowVersion")
    compile("io.arrow-kt:arrow-data:$arrowVersion")
    compile("io.arrow-kt:arrow-instances-core:$arrowVersion")
    compile("io.arrow-kt:arrow-instances-data:$arrowVersion")
    compile("io.arrow-kt:arrow-effects:$arrowVersion")
    kapt("io.arrow-kt:arrow-annotations-processor:$arrowVersion")

    compile("io.arrow-kt:arrow-effects:$arrowVersion")

}

kotlin.experimental.coroutines = Coroutines.ENABLE

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}