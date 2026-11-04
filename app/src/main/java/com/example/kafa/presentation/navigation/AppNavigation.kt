package com.example.kafa.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kafa.presentation.feature.addnote.AddNoteScreen
import com.example.kafa.presentation.feature.home.HomeScreen

@Composable
fun AppNavigation(
    viewModelFactory: ViewModelProvider.Factory
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.Home.route
    ) {
        composable(Route.Home.route) {
            HomeScreen(
                viewModelFactory = viewModelFactory,
                onAddNoteClick = { navController.navigate(Route.AddNote.route) }
            )
        }
        composable(Route.AddNote.route) {
            AddNoteScreen(
                viewModelFactory = viewModelFactory,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
