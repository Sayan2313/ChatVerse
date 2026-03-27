package com.example.chatverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatverse.screens.ChatScreen
import com.example.chatverse.screens.InboxScreen
import com.example.chatverse.screens.LoginScreen
import com.example.chatverse.screens.ProfileScreen
import com.example.chatverse.screens.SignUpScreen
import com.example.chatverse.screens.WelcomeScreen
import com.example.chatverse.ui.theme.ChatVerseTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatVerseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatApp()
                }
            }
        }
    }
}

private sealed class Route(val path: String) {
    data object Welcome : Route("welcome")
    data object Login : Route("login")
    data object SignUp : Route("signup")
    data object Inbox : Route("inbox")
    data object Profile : Route("profile")
    data object Chat : Route("chat/{chatId}") {
        fun build(chatId: String) = "chat/$chatId"
    }
}

@Preview
@Composable
fun ChatApp() {
    val nav = rememberNavController()
    
    // Check if user is already logged in
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = if (currentUser != null) Route.Inbox.path else Route.Welcome.path

    NavHost(
        navController = nav,
        startDestination = startDestination
    ) {
        composable(Route.Welcome.path) {
            WelcomeScreen(
                onGetStarted = { nav.navigate(Route.Login.path) }
            )
        }
        composable(Route.Login.path) {
            LoginScreen(
                onLoginSuccess = {
                    nav.navigate(Route.Inbox.path) {
                        // Clear backstack so user can't go back to login
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    nav.navigate(Route.SignUp.path)
                }
            )
        }
        composable(Route.SignUp.path) {
            SignUpScreen(
                onSignUpSuccess = {
                    nav.navigate(Route.Inbox.path) {
                        // Clear backstack so user can't go back to signup
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    nav.navigate(Route.Login.path)
                }
            )
        }
        composable(Route.Inbox.path) {
            InboxScreen(
                onOpenChat = { chatId -> nav.navigate(Route.Chat.build(chatId)) },
                onNavigateToProfile = { nav.navigate(Route.Profile.path) }
            )
        }
        composable(Route.Profile.path) {
            ProfileScreen(
                onBack = { nav.popBackStack() },
                onLogout = {
                    nav.navigate(Route.Login.path) {
                        // Clear backstack on logout
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Route.Chat.path,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { entry ->
            val chatId = entry.arguments?.getString("chatId").orEmpty()
            ChatScreen(
                chatId = chatId,
                onBack = { nav.popBackStack() }
            )
        }
    }
}
