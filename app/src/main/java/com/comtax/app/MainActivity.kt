package com.comtax.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.comtax.app.ui.navigation.ComtaxNavHost
import com.comtax.app.ui.theme.ComtaxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComtaxTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ComtaxNavHost()
                }
            }
        }
    }
}
