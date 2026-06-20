package com.template.app.ui.screen.susfs.content

import androidx.compose.runtime.Composable
import com.template.app.ui.LocalUiMode
import com.template.app.ui.UiMode
import com.template.app.ui.screen.susfs.content.miuix.EnabledFeaturesContentMiuix
import com.template.app.ui.screen.susfs.content.material.EnabledFeaturesContentMaterial
import com.template.app.ui.screen.susfs.util.SuSFSManager

@Composable
fun EnabledFeaturesContent(
    enabledFeatures: List<SuSFSManager.EnabledFeature>,
    onRefresh: () -> Unit
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> EnabledFeaturesContentMiuix(
            enabledFeatures = enabledFeatures,
            onRefresh = onRefresh
        )
        UiMode.Material -> EnabledFeaturesContentMaterial(
            enabledFeatures = enabledFeatures,
            onRefresh = onRefresh
        )
    }
}

