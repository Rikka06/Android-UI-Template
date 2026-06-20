package com.template.app.ui.webui

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.template.app.ui.util.listModules
import com.template.app.ui.viewmodel.SuperUserViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class WebViewInterface(private val state: WebUIState) {
    private val webView get() = state.webView
    private val modDir get() = state.modDir

    @JavascriptInterface
    fun exec(cmd: String): String {
        return "UI template: command execution disabled. Requested: ${cmd.take(120)}"
    }

    @JavascriptInterface
    fun exec(cmd: String, callbackFunc: String) {
        exec(cmd, null, callbackFunc)
    }

    @JavascriptInterface
    fun exec(
        cmd: String,
        options: String?,
        callbackFunc: String
    ) {
        val stdout = "UI template: command execution disabled. Requested: ${cmd.take(120)}"
        val jsCode =
            "javascript: (function() { try { ${callbackFunc}(0, ${JSONObject.quote(stdout)}, \"\"); } catch(e) { console.error(e); } })();"
        webView?.post {
            webView?.loadUrl(jsCode)
        }
    }

    @JavascriptInterface
    fun spawn(command: String, args: String, options: String?, callbackFunc: String) {
        val requested = buildString {
            append(command)
            if (args.isNotBlank()) append(" ").append(args)
        }.take(160)
        val jsCode =
            "javascript: (function() { try { ${callbackFunc}.stdout.emit('data', ${
                JSONObject.quote("UI template: process spawning disabled. Requested: $requested")
            }); ${callbackFunc}.emit('exit', 0); } catch(e) { console.error('spawn mock', e); } })();"
        webView?.post {
            webView?.loadUrl(jsCode)
        }
    }

    @JavascriptInterface
    fun toast(msg: String) {
        webView?.post {
            webView?.let { Toast.makeText(it.context, msg, Toast.LENGTH_SHORT).show() }
        }
    }

    @JavascriptInterface
    fun fullScreen(enable: Boolean) {
        val context = webView?.context
        if (context is Activity) {
            Handler(Looper.getMainLooper()).post {
                if (enable) {
                    hideSystemUI(context.window)
                } else {
                    showSystemUI(context.window)
                }
            }
        }
        enableEdgeToEdge(enable)
    }

    @JavascriptInterface
    fun enableEdgeToEdge(enable: Boolean = true) {
        state.isInsetsEnabled = enable
    }

    @JavascriptInterface
    fun moduleInfo(): String {
        val moduleInfos = JSONArray(listModules())
        val currentModuleInfo = JSONObject()
        currentModuleInfo.put("moduleDir", modDir)
        val moduleId = File(modDir).name
        for (i in 0 until moduleInfos.length()) {
            val currentInfo = moduleInfos.getJSONObject(i)

            if (currentInfo.getString("id") != moduleId) {
                continue
            }

            val keys = currentInfo.keys()
            for (key in keys) {
                currentModuleInfo.put(key, currentInfo.get(key))
            }
            break
        }
        return currentModuleInfo.toString()
    }

    @JavascriptInterface
    fun listPackages(type: String): String {
        val packageNames = SuperUserViewModel.apps
            .filter { appInfo ->
                val flags = appInfo.packageInfo.applicationInfo?.flags ?: 0
                when (type.lowercase()) {
                    "system" -> (flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    "user" -> (flags and ApplicationInfo.FLAG_SYSTEM) == 0
                    else -> true
                }
            }
            .map { it.packageName }
            .sorted()

        val jsonArray = JSONArray()
        for (pkgName in packageNames) {
            jsonArray.put(pkgName)
        }
        return jsonArray.toString()
    }

    @JavascriptInterface
    fun getPackagesInfo(packageNamesJson: String): String {
        val packageNames = JSONArray(packageNamesJson)
        val jsonArray = JSONArray()
        val appMap = SuperUserViewModel.apps.associateBy { it.packageName }
        for (i in 0 until packageNames.length()) {
            val pkgName = packageNames.getString(i)
            val appInfo = appMap[pkgName]
            if (appInfo != null) {
                val pkg = appInfo.packageInfo
                val app = pkg.applicationInfo
                val obj = JSONObject()
                obj.put("packageName", pkg.packageName)
                obj.put("versionName", pkg.versionName ?: "")
                obj.put("versionCode", PackageInfoCompat.getLongVersionCode(pkg))
                obj.put("appLabel", appInfo.label)
                obj.put("isSystem", if (app != null) ((app.flags and ApplicationInfo.FLAG_SYSTEM) != 0) else JSONObject.NULL)
                obj.put("uid", app?.uid ?: JSONObject.NULL)
                jsonArray.put(obj)
            } else {
                val obj = JSONObject()
                obj.put("packageName", pkgName)
                obj.put("error", "Package not found or inaccessible")
                jsonArray.put(obj)
            }
        }
        return jsonArray.toString()
    }

    @JavascriptInterface
    fun exit() {
        state.requestExit()
    }
}

fun hideSystemUI(window: Window) =
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

fun showSystemUI(window: Window) =
    WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())


