package com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.screen.MainScreen
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.ui.theme.Ghinafadiyah_607062300001_asses3mobproTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ghinafadiyah_607062300001_asses3mobproTheme {
                MainScreen()
            }
        }
    }
}