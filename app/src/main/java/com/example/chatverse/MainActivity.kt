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
import com.example.chatverse.screens.WelcomeScreen
import com.example.chatverse.ui.theme.ChatVerseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatApp()
                }
        }
    }
}
private sealed class Route(val path: String) {
    data object Welcome : Route("welcome")
    data object Inbox : Route("inbox")
    data object Chat : Route("chat/{chatId}") {
        fun build(chatId: String) = "chat/$chatId"
    }
}
@Preview
@Composable
fun ChatApp() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Route.Welcome.path
    ) {
        composable(Route.Welcome.path) {
            WelcomeScreen(
                onGetStarted = { nav.navigate(Route.Inbox.path) }
            )
        }
        composable(Route.Inbox.path) {
            ChatVerseTheme {
                InboxScreen(
                    onOpenChat = { chatId -> nav.navigate(Route.Chat.build(chatId)) }
                )
            }
        }
        composable(
            route = Route.Chat.path,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { entry ->
            val chatId = entry.arguments?.getString("chatId").orEmpty()
            ChatVerseTheme {
                ChatScreen(
                    chatId = chatId,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}