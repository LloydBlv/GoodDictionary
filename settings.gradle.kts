pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Good Dictionary"
include(":app")
include(":libs:data", ":libs:domain", ":libs:data:benchmark")
include(":ui:words-list", ":ui:splash", ":ui:word-details")
include(":feature:dictionary-sync")
include(":common:testing")
