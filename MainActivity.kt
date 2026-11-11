package com.zyranto.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.zyranto.app.ui.ZyrantoApp
import com.zyranto.app.ui.ZyrantoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZyrantoTheme { ZyrantoApp() } }
    }
}
