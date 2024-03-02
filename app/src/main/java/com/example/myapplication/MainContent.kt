package com.example.myapplication

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.example.splash.SplashScreen
import com.example.words_list.WordsListScreen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun MainContent() {


    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {

        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Screens.Splash.name,
            modifier = Modifier
        ) {
            composable(route = Screens.Splash.name) {
                SplashScreen(modifier = Modifier.fillMaxSize(),
                    navigateToWordsList = {
                        navController.navigate(Screens.WordsList.name,
                            navOptions = navOptions {
                                popUpTo(Screens.Splash.name) {
                                    inclusive = true
                                }
                            })
                    })
            }
            composable(route = Screens.WordsList.name) {
                WordsListScreen(
                    navigateToDetails = { id: Long ->
                        navController.navigate(
                            route = "${Screens.WordDetail.name}?wordId=$id",
                        )
                    }
                )
            }
            composable(
                route = "${Screens.WordDetail.name}?wordId={wordId}",
                arguments = listOf(navArgument("wordId") {
                    type = NavType.LongType
                    nullable = false
                })
            ) {
                val id = it.arguments?.getLong("wordId")!!
                com.example.word_details.WordDetailScreen(
                    modifier = Modifier.fillMaxSize(),
                    id = id,
                    onWordDeleted = navController::navigateUp,
                    onBackPressed = navController::navigateUp
                )
            }

        }
    }

}