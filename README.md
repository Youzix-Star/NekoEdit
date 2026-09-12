# NekoEdit

一个用 [miuix](https://github.com/compose-miuix-ui/miuix) 搭建的 Compose 示例应用：一个很小的文本编辑器，
用来演示 miuix 的主题、基础组件与 preference 组件。

| | |
| --- | --- |
| 包名 | `top.youzix.nekoedit` |
| minSdk | 24（Android 7.0） |
| compileSdk / targetSdk | 37 |
| UI | miuix `0.9.3` + Jetpack Compose（Compose BOM `2026.06.01`） |
| 构建 | AGP `9.4.0` / Kotlin `2.4.20` / Gradle `9.7.1` / JDK 21 |

## 演示的 miuix 能力

| 组件 | 用在哪里 |
| --- | --- |
| `MiuixTheme` + `ThemeController` | 「设置 → 外观」实时切换 跟随系统 / 浅色 / 深色 |
| `Scaffold` + `SmallTopAppBar` | 应用骨架、顶栏标题与副标题、右侧「关于」按钮 |
| `TabRow` | 「编辑 / 设置」两个页签 |
| `TextField` | 正文编辑区，字号跟随设置页的滑块 |
| `Card` / `SmallTitle` / `HorizontalDivider` | 卡片式分组布局 |
| `TextButton` + `ButtonDefaults` | 「清空 / 恢复示例」按钮 |
| `FloatingActionButton` + `SnackbarHost` | 保存草稿并弹出提示条 |
| `RadioButtonPreference` / `SliderPreference` / `SwitchPreference` / `ArrowPreference` | 设置页 |
| `OverlayDialog` | 「关于」弹窗 |
| `overScrollVertical()` | 列表的 miuix 越界回弹效果 |

## 目录结构

```
app/src/main/java/top/youzix/nekoedit/
├── MainActivity.kt        # 入口 Activity，开启 edge-to-edge
├── NekoEditApp.kt         # 主题 + Scaffold + 页签 + 全局状态
└── ui/
    ├── EditorScreen.kt    # 编辑页（TextField / Card / 统计 / 按钮）
    ├── SettingsScreen.kt  # 设置页（preference 系列组件）
    └── AboutDialog.kt     # 关于弹窗（OverlayDialog）
```

## 本地构建

```bash
# 需要 JDK 21 与 Android SDK（platform 37.0 + build-tools 37.0.0）
./gradlew assembleDebug        # 调试包
./gradlew assembleRelease      # 发布包（配置了签名后会签名）
```

签名信息可以放在项目根目录的、已被 git 忽略的 `keystore.properties` 里：

```properties
KEYSTORE_PATH=/absolute/path/to/nekoedit-release.jks
KEYSTORE_PASSWORD=...
KEY_ALIAS=nekoedit
KEY_PASSWORD=...
```

没有配置签名时，`assembleRelease` 依然会成功，只是产出未签名的 APK。

## GitHub Actions

`.github/workflows/android.yml` 会在每次 push / PR 时构建已签名的 release APK 并上传为
Artifact；推送 `v*` 标签时会额外创建 GitHub Release 并附上 APK。

签名文件与口令通过仓库 Secrets 注入，仓库里没有任何明文私钥：

| Secret | 说明 |
| --- | --- |
| `KEYSTORE_BASE64` | keystore 文件的 base64 内容 |
| `KEYSTORE_PASSWORD` | keystore 口令 |
| `KEY_ALIAS` | 密钥别名（`nekoedit`） |
| `KEY_PASSWORD` | 密钥口令 |

## 许可证

LGPL-2.1，见 [LICENSE](LICENSE)。
