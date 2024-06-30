package com.randos.graphqldemo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.randos.graphqldemo.ui.screen.AllPostsScreen
import com.randos.graphqldemo.ui.screen.PublishPostScreen

const val ALL_POST_SCREEN_DESTINATION = "all_post_screen"
const val PUBLISH_POST_SCREEN_DESTINATION = "publish_post_screen"

@Composable
fun GraphQlDemoNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ALL_POST_SCREEN_DESTINATION){

        composable(ALL_POST_SCREEN_DESTINATION){
            AllPostsScreen(onAdd = {
                navController.navigate(PUBLISH_POST_SCREEN_DESTINATION)
            })
        }

        composable(PUBLISH_POST_SCREEN_DESTINATION){
            PublishPostScreen(onPostPublished = {
                navController.popBackStack()
            })
        }
    }
}