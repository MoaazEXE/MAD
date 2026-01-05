pluginManagement {
    repositories {
        google()  // ← Remove the content filters
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode. set(RepositoriesMode. FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MAD"
include(":app")