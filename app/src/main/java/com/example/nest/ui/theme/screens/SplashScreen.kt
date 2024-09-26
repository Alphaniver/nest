package com.example.nest.ui.theme.screens


import android.net.Uri
import android.view.ViewGroup
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import com.example.nest.navigation.ROUTE_HOME
import com.example.nest.navigation.ROUTE_SPLASH

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        delay(4000) // Delay for the duration of your video
        navController.navigate(ROUTE_HOME) {
            popUpTo(ROUTE_SPLASH) { inclusive = true }
        }
    }

    AndroidView(factory = {
        VideoView(context).apply {
            val uri: Uri = Uri.parse("android.resource://${context.packageName}/raw/splash")
            setVideoURI(uri)
            start()
            setOnCompletionListener {
                navController.navigate(ROUTE_HOME) {
                    popUpTo(ROUTE_SPLASH) { inclusive = true }
                }
            }
        }
    }, update = { videoView ->
        videoView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    })
}
