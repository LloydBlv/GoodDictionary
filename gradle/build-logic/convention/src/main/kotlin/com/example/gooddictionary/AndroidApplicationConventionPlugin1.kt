package com.example.gooddictionary

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin1 : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }
            configureAndroid()
        }
    }
}