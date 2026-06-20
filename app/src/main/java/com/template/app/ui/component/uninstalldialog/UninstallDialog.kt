package com.template.app.ui.component.uninstalldialog

import androidx.compose.runtime.Composable
import com.template.app.ui.LocalUiMode
import com.template.app.ui.UiMode

@Composable
fun UninstallDialog(
    show: Boolean,
    onDismissRequest: () -> Unit
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> UninstallDialogMiuix(show, onDismissRequest)
        UiMode.Material -> UninstallDialogMaterial(show, onDismissRequest)
    }
}

