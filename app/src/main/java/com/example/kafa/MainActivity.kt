package com.example.kafa

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.kafa.presentation.navigation.AppNavigation
import com.example.kafa.ui.theme.KafaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (application as KafaApp).appComponent
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            KafaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = KafaTheme.colors.screenBackground,
                    contentColor = KafaTheme.colors.textPrimary,
                ) {
                    AppNavigation(
                        viewModelFactory = appComponent.viewModelFactory()
                    )
                }
            }
        }
    }
}
