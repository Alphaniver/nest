package com.example.nest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nest.ui.theme.screens.SplashScreen
import com.example.nest.ui.theme.screens.home.FirstScreen
import com.example.nest.ui.theme.screens.landlord.EditApartment
import com.example.nest.ui.theme.screens.landlord.Landlord
import com.example.nest.ui.theme.screens.landlord.LandlordPost
import com.example.nest.ui.theme.screens.landlord.ViewApartmentsScreen
import com.example.nest.ui.theme.screens.tenant.TenantScreen

@Composable
fun AppNavHost(
    navController: NavHostController= rememberNavController(),
    startDestination: String= ROUTE_SPLASH
){
    NavHost(navController = navController, startDestination=startDestination){
        composable(ROUTE_HOME){ FirstScreen(navController) }
        composable(ROUTE_SPLASH){ SplashScreen(navController)}
        composable(ROUTE_ADD_APARTMENT){ LandlordPost(navController) }
        composable(ROUTE_TENANT_WELCOME){ TenantScreen(navController) }
        composable(ROUTE_LANDLORD_LOGIN){ Landlord(navController) }
        composable(ROUTE_VIEW_APARTMENTS){ ViewApartmentsScreen(navController) }
        composable(ROUTE_EDIT){ EditApartment(navController, id = "") }


    }


}