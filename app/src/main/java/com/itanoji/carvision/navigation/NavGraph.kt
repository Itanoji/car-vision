package com.itanoji.carvision.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.itanoji.carvision.ui.inspection.createInspection.CreateInspectionScreen
import com.itanoji.carvision.ui.inspection.inscpetionDetail.InspectionDetailScreen
import com.itanoji.carvision.ui.inspections.InspectionsListScreen
import com.itanoji.carvision.ui.login.LoginScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object InspectionsList : Screen("inspections_list")
    object InspectionDetail : Screen("inspection_detail")
    object CreateInspection: Screen("create_inspection")
    object Camera : Screen("camera")
}

@Composable
fun NavGraph(startDestination: String = Screen.InspectionsList.route) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.InspectionsList.route) {
            InspectionsListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate("${Screen.InspectionDetail.route}?id=$id")
                },
                onCreateInspection = {
                    navController.navigate(Screen.CreateInspection.route)
                }
            )
        }
        composable(
            route = Screen.InspectionDetail.route + "?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    nullable = false
                }
            )

        ) { entry ->
            entry.arguments?.getLong("id")
                ?.let { InspectionDetailScreen(navController = navController, inspectionId = it) }
        }

        composable(Screen.CreateInspection.route) {
            CreateInspectionScreen(navController = navController)
        }
    }
}
