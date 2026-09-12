# NekoEdit

一个用 [miuix](https://github.com/compose-miuix-ui/miuix) 搭建的 Compose 示例应用：一个极简文本编辑器。
界面与交互参考了 [InstallerX-Revived](https://github.com/wxxsfxyzm/InstallerX-Revived) 的设计——
底部是 miuix 的 **液态玻璃悬浮底盘**，主页是卡片式列表，关于页是「大 logo + 卡片项」的结构。

| | |
| --- | --- |
| 包名 | `top.youzix.nekoedit` |
| minSdk | 33（Android 13，`miuix-blur` 的硬性要求） |
| compileSdk / targetSdk | 37 |
| UI | miuix `0.9.3` + Compose BOM `2026.06.01` + Material Icons Extended |
| 构建 | AGP `9.4.0`（内置 Kotlin）/ Kotlin `2.4.20` / Gradle `9.7.1` / JDK 21 |

## 四个页签

底栏是四个页签，页面之间用 `HorizontalPager` 横向切换：

| 页签 | 内容 |
| --- | --- |
| 主页 | 概览卡片（草稿数 / 总字数 / 最近编辑）+ 卡片式草稿列表，可新建、打开、删除 |
| 编辑器 | 标题 + 正文输入，实时写入草稿；可选的统计卡片；FAB 保存 |
| 设置 | 6 种主题模式（含 Monet 动态取色）、液态玻璃开关、字号、自动保存、统计卡片 |
| 关于 | 大 logo 头部 + 获取源代码 / 开源许可 / 检查更新 + 开发者与反馈 |

## 液态玻璃悬浮底盘

底栏是从 miuix 官方示例的 `IosLiquidGlassNavigationBar` 移植过来的，源码位于
`app/src/main/java/top/youzix/nekoedit/ui/liquid/`（Apache-2.0，见文件头注释）：

| 文件 | 作用 |
| --- | --- |
| `FloatingBottomBar.kt` | 悬浮胶囊底栏本体：拖拽跟随、指示器、玻璃高光 |
| `Lens.kt` | 用 AGSL runtime shader 做的边缘折射（真正的「液态玻璃」） |
| `InnerShadow.kt` | 玻璃内阴影 |
| `CombinedBackdrop.kt` | 把页面内容与底栏自身合成为一张 backdrop |
| `Vibrancy.kt` | 色彩增强 |
| `../animation/DampedDragAnimation.kt` | 带阻尼的拖拽动画 |
| `../animation/InteractiveHighlight.kt` | 触摸处的交互高光 |

接法（`NekoEditApp.kt`）：页面内容用 `Modifier.layerBackdrop(backdrop)` 录进 `rememberLayerBackdrop`，
底栏拿着同一个 backdrop 做实时模糊与折射，所以滑动内容时玻璃里透出的是真实背景。

依赖 `miuix-blur`，它要求 **minSdk 33**；因为用了 `material-icons-extended`，
release 构建开启了 R8，把没用到的一万多个图标裁掉（APK 从 44 MB 降到约 2.8 MB）。

## 目录结构

```
app/src/main/java/top/youzix/nekoedit/
├── MainActivity.kt              # 入口 Activity，开启 edge-to-edge
├── NekoEditApp.kt               # 主题 + Scaffold + Pager + 液态玻璃底栏
├── data/
│   ├── Draft.kt                 # 草稿模型
│   └── DraftStore.kt            # 进程内状态 + SharedPreferences 持久化
└── ui/
    ├── AppTheme.kt              # MiuixTheme + 明暗判定 + 主题模式列表
    ├── AppIcons.kt              # 用到的 Material 图标
    ├── home/HomeScreen.kt       # 主页
    ├── editor/EditorScreen.kt   # 编辑器
    ├── settings/SettingsScreen.kt
    ├── about/AboutScreen.kt     # 关于页（含检查更新）
    ├── about/LicensesScreen.kt  # 开源许可
    ├── update/UpdateChecker.kt  # GitHub Releases API
    ├── liquid/                  # 液态玻璃组件（移植自 miuix 示例）
    └── animation/               # 动画辅助（移植自 miuix 示例）
```

## 本地构建

```bash
# 需要 JDK 21 与 Android SDK（platform 37.0 + build-tools 37.0.0）
./gradlew assembleDebug
./gradlew assembleRelease      # 配置了签名后会产出已签名 APK
```

签名信息放在项目根目录、已被 git 忽略的 `keystore.properties` 里（模板见 `keystore.properties.example`）：

```properties
KEYSTORE_PATH=/absolute/path/to/nekoedit-release.jks
KEYSTORE_PASSWORD=...
KEY_ALIAS=nekoedit
KEY_PASSWORD=...
```

没有配置签名时 `assembleRelease` 依然成功，只是产出未签名的 APK。

## GitHub Actions

`.github/workflows/android.yml` 在每次 push / PR 时构建已签名的 release APK 并上传为 Artifact；
推送 `v*` 标签时额外创建 GitHub Release 并附上 APK。

签名文件与口令通过仓库 Secrets 注入，仓库里没有任何明文私钥：

| Secret | 说明 |
| --- | --- |
| `KEYSTORE_BASE64` | keystore 文件的 base64 内容 |
| `KEYSTORE_PASSWORD` | keystore 口令 |
| `KEY_ALIAS` | 密钥别名（`nekoedit`） |
| `KEY_PASSWORD` | 密钥口令 |

## 致谢

- [miuix](https://github.com/compose-miuix-ui/miuix)（Apache-2.0）：组件与液态玻璃参考实现
- [InstallerX-Revived](https://github.com/wxxsfxyzm/InstallerX-Revived)（GPL-3.0）：界面结构与交互参考
- [AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass)（Apache-2.0）：液态玻璃的原始实现

## 许可证

LGPL-2.1，见 [LICENSE](LICENSE)。
