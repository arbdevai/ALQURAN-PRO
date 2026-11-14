package com.alquran.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alquran.pro.ui.MainApp
import com.alquran.pro.ui.theme.AlQuranProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlQuranProTheme {
                MainApp()
            }
        }
    }
}
