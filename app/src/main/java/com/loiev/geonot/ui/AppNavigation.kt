package com.loiev.geonot.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.loiev.geonot.GeonotApplication
import com.loiev.geonot.ui.screens.AddNoteScreen
import com.loiev.geonot.ui.screens.MapScreen
import com.loiev.geonot.ui.screens.NotesListScreen
import com.loiev.geonot.ui.viewmodels.NotesViewModel
import com.loiev.geonot.ui.viewmodels.ViewModelFactory

sealed class Screen(val route: String, val icon: ImageVector, val title: String) {
    object Map : Screen("map", Icons.Default.Home, "Map")
    object NotesList : Screen("notes_list", Icons.AutoMirrored.Filled.List, "Markers")
    object AddNote : Screen("add_note?lat={lat}&lng={lng}", Icons.Default.Add, "Add") {
        fun createRoute(lat: Double, lng: Double) = "add_note?lat=$lat&lng=$lng"
    }
    object Profile : Screen("profile", Icons.Default.Person, "Profile")
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val screens = listOf(Screen.Map, Screen.NotesList, Screen.AddNote, Screen.Profile)

    Scaffold(bottomBar = { BottomNavigationBar(navController = navController, items = screens) }) { innerPadding ->
        AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    // Створюємо ViewModel один раз для всього графу навігації
    val application = LocalContext.current.applicationContext as GeonotApplication
    val notesViewModel: NotesViewModel = viewModel(
        factory = ViewModelFactory(application.repository)
    )

    NavHost(navController = navController, startDestination = Screen.Map.route, modifier = modifier) {
        composable(Screen.Map.route) {
            MapScreen(viewModel = notesViewModel, navController = navController)
        }
        composable(Screen.NotesList.route) { NotesListScreen(viewModel = notesViewModel) }
        // Передаємо viewModel та navController в екран додавання
        composable(
            route = Screen.AddNote.route,
            arguments = listOf(
                navArgument("lat") {
                    type = NavType.FloatType
                    defaultValue = 0f
                },
                navArgument("lng") {
                    type = NavType.FloatType
                    defaultValue = 0f
                }
            )
        ) { backStackEntry ->
            // Витягуємо аргументи
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 0.0

            // Передаємо їх в AddNoteScreen
            AddNoteScreen(
                navController = navController,
                viewModel = notesViewModel,
                latitude = lat,
                longitude = lng
            )
        }
        composable(Screen.Profile.route) { PlaceholderScreen("Profile Screen") }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, items: List<Screen>) {
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}