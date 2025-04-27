package com.itanoji.carvision.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.itanoji.carvision.navigation.Screen

@Composable
fun LoginScreen(navController: NavController) {
    Text(text = "Login Screen")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            navController.navigate(Screen.InspectionsList.route)
        }) {
            Text("Login")
        }
    }
}