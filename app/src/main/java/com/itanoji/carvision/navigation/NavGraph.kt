package com.itanoji.carvision.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.itanoji.carvision.domain.model.InspectionResult
import com.itanoji.carvision.ui.camera.CameraScreen
import com.itanoji.carvision.ui.camera.RequestCameraPermission
import com.itanoji.carvision.ui.inspection.create.CreateInspectionScreen
import com.itanoji.carvision.ui.inspection.edit.EditInspectionScreen
import com.itanoji.carvision.ui.inspection.view.InspectionDetailScreen
import com.itanoji.carvision.ui.inspection_result.InspectionResultScreen
import com.itanoji.carvision.ui.inspections.InspectionsListScreen
import com.itanoji.carvision.ui.login.LoginScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object InspectionsList : Screen("inspections_list")
    object InspectionDetail : Screen("inspection_detail")
    object CreateInspection : Screen("create_inspection")
    object EditInspection : Screen("edit_inspection")
    object Camera : Screen("camera")
    object InspectionResult : Screen("inspection_result")
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

        composable(
            route = Screen.EditInspection.route + "?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    nullable = false
                }
            )

        ) { entry ->
            entry.arguments?.getLong("id")
                ?.let { EditInspectionScreen(navController = navController, inspectionId = it) }
        }

        composable(
            route = Screen.Camera.route + "?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    nullable = false
                }
            )

        ) { entry ->
            entry.arguments?.getLong("id")
                ?.let {
                    var permissionGranted by remember { mutableStateOf(false) }

                    RequestCameraPermission {
                        permissionGranted = true
                    }

                    if (permissionGranted) {
                        CameraScreen(navController = navController, inspectionId = it)
                    }
                }
        }

        composable(
            route = Screen.InspectionResult.route + "?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    nullable = false
                }
            )

        ) { entry ->
            entry.arguments?.getLong("id")
                ?.let { InspectionResultScreen(navController = navController, resultId = it) }
        }
    }
}
