package com.template.app.ui.webui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.template.app.R
import com.template.app.data.repository.ModuleRepositoryImpl
import com.template.app.ui.util.AppIconCache
import com.template.app.ui.util.withMainUserUid
import com.template.app.ui.viewmodel.SuperUserViewModel
import java.io.File

fun Activity.setTaskDescription(label: String) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        @Suppress("DEPRECATION")
        setTaskDescription(ActivityManager.TaskDescription(label))
    } else {
        val taskDescription = ActivityManager.TaskDescription.Builder()
            .setLabel(label)
            .build()
        setTaskDescription(taskDescription)
    }
}

@SuppressLint("SetJavaScriptEnabled")
internal suspend fun prepareWebView(
    activity: Activity,
    moduleId: String,
    webUIState: WebUIState,
) {
    withContext(Dispatchers.IO) {
        val repo = ModuleRepositoryImpl()
        val modules = repo.getModules().getOrDefault(emptyList())
        val moduleInfo = modules.find { info -> info.id == moduleId }

        if (moduleInfo == null) {
            withContext(Dispatchers.Main) {
                webUIState.uiEvent = WebUIEvent.Error(activity.getString(R.string.no_such_module, moduleId))
            }
            return@withContext
        }

        if (!moduleInfo.hasWebUi || !moduleInfo.enabled || moduleInfo.update || moduleInfo.remove) {
            withContext(Dispatchers.Main) {
                webUIState.uiEvent = WebUIEvent.Error(activity.getString(R.string.module_unavailable, moduleInfo.name))
            }
            return@withContext
        }

        webUIState.moduleName = moduleInfo.name
        webUIState.modDir = File(activity.filesDir, "template_modules/$moduleId").absolutePath

        if (SuperUserViewModel.apps.isEmpty()) {
            SuperUserViewModel().fetchAppList()
        }
        webUIState.rootShell = null

        val webRoot = File(webUIState.modDir, "webroot").apply { mkdirs() }
        File(webRoot, "index.html").writeText(
            """
            <!doctype html>
            <html>
            <head>
              <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
              <title>${moduleInfo.name}</title>
              <style>
                :root { color-scheme: light dark; font-family: system-ui, sans-serif; }
                body { margin: 0; min-height: 100vh; display: grid; place-items: center; background: transparent; }
                main { width: min(520px, calc(100vw - 48px)); }
                h1 { margin: 0 0 12px; font-size: 28px; line-height: 1.15; }
                p { margin: 0 0 14px; opacity: .78; line-height: 1.55; }
                button { border: 0; border-radius: 12px; padding: 11px 14px; font: inherit; }
              </style>
            </head>
            <body>
              <main>
                <h1>${moduleInfo.name}</h1>
                <p>SukiSU Ultra UI template WebUI. Module files, shell commands, and remote content are disabled.</p>
                <p>Module ID: ${moduleInfo.id}</p>
                <button onclick="ksu.toast('UI template only')">Template Action</button>
              </main>
            </body>
            </html>
            """.trimIndent()
        )

        withContext(Dispatchers.Main) {
            activity.setTaskDescription(activity.getString(R.string.app_name) + " - ${moduleInfo.name}")

            val webView = WebView(activity)
            webView.setBackgroundColor(Color.TRANSPARENT)

            val prefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE)
            WebView.setWebContentsDebuggingEnabled(prefs.getBoolean("enable_web_debugging", false))

            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = false
            }

            val webViewAssetLoader = WebViewAssetLoader.Builder()
                .setDomain("mui.kernelsu.org")
                .addPathHandler(
                    "/",
                    SuFilePathHandler(
                        activity,
                        webRoot,
                        null,
                        { webUIState.currentInsets },
                        { enable -> webUIState.isInsetsEnabled = enable })
                )
                .build()

            // WebViewClient
            webView.webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                    val url = request.url
                    if (url.scheme.equals("ksu", ignoreCase = true) && url.host.equals("icon", ignoreCase = true)) {
                        val packageName = url.path?.substring(1)
                        if (!packageName.isNullOrEmpty()) {
                            val appInfo = SuperUserViewModel.apps
                                .find { it.packageName == packageName }
                                ?.packageInfo?.applicationInfo
                            if (appInfo != null) {
                                val icon = AppIconCache.loadIconSync(activity, appInfo.withMainUserUid(activity), 512)
                                val stream = java.io.ByteArrayOutputStream()
                                icon.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
                                return WebResourceResponse(
                                    "image/png", null, 200, "OK",
                                    mapOf("Access-Control-Allow-Origin" to "*"),
                                    java.io.ByteArrayInputStream(stream.toByteArray())
                                )
                            } else {
                                val errorMsg = "No such package"
                                val errorStream = java.io.ByteArrayInputStream(errorMsg.toByteArray(Charsets.UTF_8))
                                return WebResourceResponse(
                                    "text/plain",
                                    "utf-8",
                                    404,
                                    "Not Found",
                                    mapOf("Access-Control-Allow-Origin" to "*"),
                                    errorStream
                                )
                            }
                        }
                    }
                    return webViewAssetLoader.shouldInterceptRequest(url)
                }

                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    webUIState.webCanGoBack = view?.canGoBack() ?: false
                    if (webUIState.isInsetsEnabled) webUIState.webView?.evaluateJavascript(webUIState.currentInsets.js, null)
                    super.doUpdateVisitedHistory(view, url, isReload)
                }
            }

            // WebChromeClient
            webView.webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    if (message == null || result == null) return false
                    webUIState.uiEvent = WebUIEvent.ShowAlert(message, result)
                    return true
                }

                override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    if (message == null || result == null) return false
                    webUIState.uiEvent = WebUIEvent.ShowConfirm(message, result)
                    return true
                }

                override fun onJsPrompt(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    defaultValue: String?,
                    result: JsPromptResult?
                ): Boolean {
                    if (message == null || result == null || defaultValue == null) return false
                    webUIState.uiEvent = WebUIEvent.ShowPrompt(message, defaultValue, result)
                    return true
                }

                override fun onShowFileChooser(
                    webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?
                ): Boolean {
                    webUIState.filePathCallback?.onReceiveValue(null)
                    webUIState.filePathCallback = filePathCallback

                    val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" }
                    if (fileChooserParams?.mode == FileChooserParams.MODE_OPEN_MULTIPLE) {
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }
                    webUIState.uiEvent = WebUIEvent.ShowFileChooser(intent)
                    return true
                }
            }

            // JS Interface
            val webviewInterface = WebViewInterface(webUIState)
            webUIState.webView = webView
            webView.addJavascriptInterface(webviewInterface, "ksu")
            webUIState.uiEvent = WebUIEvent.WebViewReady
        }
    }
}

