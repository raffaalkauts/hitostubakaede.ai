package com.kaede.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaede.app.ui.chat.ChatScreen
import com.kaede.app.ui.theme.KaedeTheme
import com.kaede.app.viewmodel.ChatViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModelFactory: ChatViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize ViewModel Factory
        viewModelFactory = ChatViewModelFactory(application)
        
        enableEdgeToEdge()
        setContent {
            KaedeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel(factory = viewModelFactory)
                    )
                }
            }
        }
    }
}
