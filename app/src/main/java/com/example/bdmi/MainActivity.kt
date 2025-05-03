package com.example.bdmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.bdmi.data.utils.VoiceToTextParser
import com.example.bdmi.navigation.RootNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val voiceToTextParser by lazy {
        VoiceToTextParser(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            RootNavigation(voiceToTextParser)
        }
    }
}

@Composable
fun RootNavigation(voiceToTextParser: VoiceToTextParser) {
    val navController = rememberNavController()
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val loggedIn = sessionViewModel.isLoggedIn.collectAsState()
    val isInitialized = sessionViewModel.isInitialized.collectAsState()

    // TODO: Splash Screen?
    if (!isInitialized.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        RootNavGraph(
            navController = navController,
            loggedIn = loggedIn.value,
            sessionViewModel = sessionViewModel,
            voiceToTextParser = voiceToTextParser
        )
    }
}