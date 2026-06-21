# Android Compose UI Template

这是一个纯净的 **Android Jetpack Compose UI 模板** 项目。它被设计为一个开箱即用的现代化 Android 界面脚手架，移除了所有不需要的后台逻辑、特权应用(Root)探测与遗留的 Java 代码。

## ✨ 特性 (Features)

- **100% 纯 Kotlin 架构**：移除所有 Java 历史包袱，全面拥抱现代 Kotlin 语法与协程。
- **现代化 UI 引擎**：基于 `Jetpack Compose` 构建整个应用界面。
- **主题支持**：内嵌对 `Material 3` 与 `Miuix` 风格库的兼容与支持，轻松实现动态主题响应与适配。
- **纯粹的前端展示**：该模板移除了底层服务端依赖、系统组件服务等所有非 UI 层的累赘代码，非常适合直接作为独立 App 或新项目的静态页面壳。
- **全面的中文化与占位脱敏**：包含全面且易于二次修改的 `strings.xml` 中文资源，无敏感业务逻辑残留。

## 🚀 如何使用 (Getting Started)

1. **克隆项目到本地：**
   ```bash
   git clone https://github.com/Rikka06/Android-UI-Template.git
   ```

2. **使用 Android Studio 打开项目：**
   推荐使用最新的 Android Studio (Ladybug 或更新版本) 打开本目录。

3. **构建与运行：**
   项目已配置好无报错的 Gradle 编译环境，直接点击右上角的 `▶ Run` 按钮，或在终端中运行以下命令打包 APK：
   ```bash
   ./gradlew assembleDebug
   ```
   打包完成后，你可以在 `app/build/outputs/apk/debug/` 目录找到可安装的 `app-debug.apk`。

## 🛠 技术栈 (Tech Stack)

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **架构**: MVVM (ViewModel + StateFlow)
- **构建工具**: Gradle (Kotlin DSL)

## 📝 证书 (License)

本项目仅作为基础模板使用，您可以在此基础上随意修改和二次分发。
