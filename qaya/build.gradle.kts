plugins {
    kotlin("jvm") version "1.9.22"
}

repositories {
    google()
    maven { url = uri("https://repo1.maven.org/maven2/") }
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test"))
}
