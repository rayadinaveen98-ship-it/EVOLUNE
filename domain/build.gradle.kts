plugins {
    id("org.jetbrains.kotlin.jvm")
}
kotlin { jvmToolchain(17) }

// Regression tests for the dependency-free scheduling engine.
dependencies { testImplementation("junit:junit:4.13.2") }
tasks.test { useJUnit() }
