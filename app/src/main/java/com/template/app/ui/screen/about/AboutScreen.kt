package com.template.app.ui.screen.about

import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.dropUnlessResumed
import com.template.app.BuildConfig
import com.template.app.R
import com.template.app.ui.LocalUiMode
import com.template.app.ui.UiMode
import com.template.app.ui.navigation3.LocalNavigator

@Composable
fun AboutScreen() {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    val htmlString = stringResource(
        id = R.string.about_source_code,
        "<b><a href=\"https://github.com/ShirkNeko/SukiSU-Ultra\">GitHub</a></b>",
        "<b><a href=\"https://t.me/SukiKSU\">Telegram</a></b>",
        "<b>鎬″瓙鏇版洶</b>",
        "<b>鏄庨 OuO</b>",
        "<b><a href=\"https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode.txt\">CC BY-NC-SA 4.0</a></b>"
    )
    val state = AboutUiState(
        title = stringResource(R.string.about),
        appName = stringResource(R.string.app_name),
        versionName = BuildConfig.VERSION_NAME,
        links = extractLinks(htmlString),
    )
    val actions = AboutScreenActions(
        onBack = dropUnlessResumed { navigator.pop() },
        onOpenLink = {
            Toast.makeText(context, "UI template: external links are disabled.", Toast.LENGTH_SHORT).show()
        },
    )

    when (LocalUiMode.current) {
        UiMode.Miuix -> AboutScreenMiuix(state, actions)
        UiMode.Material -> AboutScreenMaterial(state, actions)
    }
}

