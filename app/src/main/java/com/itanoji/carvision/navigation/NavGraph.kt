package com.itanoji.carvision.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.itanoji.carvision.ui.inspection.createInspection.CreateInspectionScreen
import com.itanoji.carvision.ui.inspections.InspectionsListScreen
import com.itanoji.carvision.ui.login.LoginScreen
import com.itanoji.carvision.ui.inspection.inscpetionDetail.InspectionScreen

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
                    navController.navigate("${Screen.InspectionDetail.route}/$id")
                },
                onCreateInspection = {
                    navController.navigate(Screen.CreateInspection.route)
                }
            )
        }
        composable(Screen.InspectionDetail.route) {
            InspectionScreen()
        }
        composable(Screen.CreateInspection.route) {
            CreateInspectionScreen(navController = navController)
        }
    }
}
