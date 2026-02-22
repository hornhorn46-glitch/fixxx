package com.example.finalxaurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.finalxaurora.app.AppContainer
import com.example.finalxaurora.ui.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val container = AppContainer.create(applicationContext)

        setContent {
            App(vmFactory = container.vmFactory, settings = container.settings)
        }
    }
}
