package com.ndejje.lostfoundhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ndejje.lostfoundhub.data.model.Item
import com.ndejje.lostfoundhub.ui.screens.AddItemScreen
import com.ndejje.lostfoundhub.ui.screens.ChatListScreen
import com.ndejje.lostfoundhub.ui.screens.ChatScreen
import com.ndejje.lostfoundhub.ui.screens.HomeScreen
import com.ndejje.lostfoundhub.ui.screens.ItemDetailScreen
import com.ndejje.lostfoundhub.ui.screens.LoginScreen
import com.ndejje.lostfoundhub.ui.screens.MyItemsScreen
import com.ndejje.lostfoundhub.ui.screens.SignUpScreen
import com.ndejje.lostfoundhub.ui.theme.LostFoundHubTheme
import com.ndejje.lostfoundhub.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LostFoundHubTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val authViewModel = remember { AuthViewModel() }
    var isAuthenticated by remember { mutableStateOf(authViewModel.isAuthenticated) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var selectedChatId by remember { mutableStateOf<String?>(null) }
    var selectedChatReceiverId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authViewModel.isAuthenticated) {
        isAuthenticated = authViewModel.isAuthenticated
        if (isAuthenticated) currentScreen = Screen.Home
    }

    when (currentScreen) {
        Screen.Login -> LoginScreen(
            onLoginSuccess = { currentScreen = Screen.Home },
            onNavigateToSignUp = { currentScreen = Screen.SignUp }
        )
        Screen.SignUp -> SignUpScreen(
            onSignUpSuccess = { currentScreen = Screen.Home },
            onNavigateToLogin = { currentScreen = Screen.Login }
        )
        Screen.Home -> HomeScreen(
            onItemClick = { selectedItem = it; currentScreen = Screen.ItemDetail },
            onAddItemClick = { currentScreen = Screen.AddItem },
            onMessagesClick = { currentScreen = Screen.ChatList },
            onMyItemsClick = { currentScreen = Screen.MyItems },
            onLogout = { authViewModel.signOut(); isAuthenticated = false; currentScreen = Screen.Login }
        )
        Screen.AddItem -> AddItemScreen(
            onItemAdded = { currentScreen = Screen.Home },
            onBack = { currentScreen = Screen.Home }
        )
        Screen.ItemDetail -> ItemDetailScreen(
            item = selectedItem!!,
            currentUserId = authViewModel.getCurrentUserId(),
            onBack = { currentScreen = Screen.Home },
            onStartChat = { chatId, receiverId, _ ->
                selectedChatId = chatId
                selectedChatReceiverId = receiverId
                currentScreen = Screen.Chat
            },
            onMarkResolved = { currentScreen = Screen.Home }
        )
        Screen.ChatList -> ChatListScreen(
            onChatClick = { chatId, receiverId ->
                selectedChatId = chatId
                selectedChatReceiverId = receiverId
                currentScreen = Screen.Chat
            },
            onBack = { currentScreen = Screen.Home }
        )
        Screen.Chat -> ChatScreen(
            chatId = selectedChatId ?: "",
            receiverId = selectedChatReceiverId ?: "",
            onBack = { currentScreen = Screen.ChatList }
        )
        Screen.MyItems -> MyItemsScreen(
            onItemClick = { selectedItem = it; currentScreen = Screen.ItemDetail },
            onBack = { currentScreen = Screen.Home }
        )
    }
}

sealed class Screen {
    object Login : Screen()
    object SignUp : Screen()
    object Home : Screen()
    object AddItem : Screen()
    object ItemDetail : Screen()
    object ChatList : Screen()
    object Chat : Screen()
    object MyItems : Screen()
}
