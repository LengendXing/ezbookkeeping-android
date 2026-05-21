package com.ezbookkeeping.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ezbookkeeping.android.ui.navigation.EZBookkeepingNavHost
import com.ezbookkeeping.android.ui.theme.EZBookkeepingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EZBookkeepingTheme {
                EZBookkeepingNavHost()
            }
        }
    }
}
