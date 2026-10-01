package com.nexo.banca

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.nexo.banca.ui.NexoRoot
import com.nexo.banca.ui.NexoViewModel
import com.nexo.banca.ui.theme.NexoTheme
import com.nexo.banca.ui.theme.Paper
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    private val viewModel: NexoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        setContent {
            val state by viewModel.state.collectAsState()
            NexoTheme(themeMode = state.preferences.themeMode) {
                Surface(modifier = Modifier.fillMaxSize(), color = Paper) {
                    NexoRoot(state = state, viewModel = viewModel)
                }
            }
        }
    }
}
