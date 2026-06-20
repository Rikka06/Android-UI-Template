package com.template.app.ui.screen.susfs

import androidx.compose.runtime.Composable
import com.template.app.ui.LocalUiMode
import com.template.app.ui.UiMode

@Composable
fun SuSFSScreen() {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SuSFSMiuix()
        UiMode.Material -> SuSFSMaterial()
    }
}

