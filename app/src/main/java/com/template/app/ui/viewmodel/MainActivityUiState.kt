package com.template.app.ui.viewmodel

import androidx.compose.runtime.Immutable
import com.template.app.ui.UiMode
import com.template.app.ui.theme.AppSettings

@Immutable
data class MainActivityUiState(
    val appSettings: AppSettings,
    val pageScale: Float,
    val enableBlur: Boolean,
    val enableFloatingBottomBar: Boolean,
    val enableFloatingBottomBarBlur: Boolean,
    val uiMode: UiMode,
)

