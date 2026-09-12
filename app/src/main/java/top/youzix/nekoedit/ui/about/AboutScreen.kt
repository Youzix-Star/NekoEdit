/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.youzix.nekoedit.BuildConfig
import top.youzix.nekoedit.R
import top.youzix.nekoedit.ui.AppIcons
import top.youzix.nekoedit.ui.update.ReleaseInfo
import top.youzix.nekoedit.ui.update.UpdateChecker

private const val REPOSITORY_URL = "https://github.com/Youzix-Star/NekoEdit"
private const val DEVELOPER_URL = "https://github.com/Youzix-Star"
private const val FEEDBACK_EMAIL = "youzix.star@gmail.com"

private sealed interface UpdateState {
    data class Available(val info: ReleaseInfo) : UpdateState
    data class UpToDate(val info: ReleaseInfo) : UpdateState
    data class Failed(val message: String) : UpdateState
}

@Composable
fun AboutScreen(
    contentPadding: PaddingValues,
    onOpenLicenses: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()

    var checkingUpdate by remember { mutableStateOf(false) }
    var updateState by remember { mutableStateOf<UpdateState?>(null) }

    fun checkForUpdates() {
        if (checkingUpdate) return
        checkingUpdate = true
        coroutineScope.launch {
            val result = UpdateChecker.fetchLatest()
            checkingUpdate = false
            updateState = result.fold(
                onSuccess = { info ->
                    if (UpdateChecker.isNewer(BuildConfig.VERSION_NAME, info.version)) {
                        UpdateState.Available(info)
                    } else {
                        UpdateState.UpToDate(info)
                    }
                },
                onFailure = { error -> UpdateState.Failed(error.message ?: "未知错误") },
            )
        }
    }

    LazyColumn(
        modifier = modifier.overScrollVertical(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "header") {
            AppHeader()
        }

        item(key = "about") {
            Column {
                SmallTitle(text = "关于")
                Card(modifier = Modifier.fillMaxWidth()) {
                    ArrowPreference(
                        title = "获取源代码",
                        summary = "在 GitHub 上查看 NekoEdit 的源码",
                        startAction = {
                            Icon(
                                imageVector = AppIcons.SourceCode,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        onClick = { uriHandler.openUri(REPOSITORY_URL) },
                    )
                    ArrowPreference(
                        title = "开源许可",
                        summary = "miuix、Compose、Material Icons 等依赖的许可证",
                        startAction = {
                            Icon(
                                imageVector = AppIcons.License,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        onClick = onOpenLicenses,
                    )
                    ArrowPreference(
                        title = if (checkingUpdate) "正在检查更新…" else "检查更新",
                        summary = "从 GitHub Releases 获取最新版本",
                        startAction = {
                            Icon(
                                imageVector = if (checkingUpdate) AppIcons.Refresh else AppIcons.Update,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        enabled = !checkingUpdate,
                        onClick = { checkForUpdates() },
                    )
                }
            }
        }

        item(key = "developer") {
            Column {
                SmallTitle(text = "开发者")
                Card(modifier = Modifier.fillMaxWidth()) {
                    ArrowPreference(
                        title = "Youzix-Star",
                        summary = "github.com/Youzix-Star",
                        startAction = {
                            Icon(
                                imageVector = AppIcons.Developer,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        onClick = { uriHandler.openUri(DEVELOPER_URL) },
                    )
                    ArrowPreference(
                        title = "反馈与建议",
                        summary = FEEDBACK_EMAIL,
                        startAction = {
                            Icon(
                                imageVector = AppIcons.Feedback,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        onClick = { uriHandler.openUri("mailto:$FEEDBACK_EMAIL") },
                    )
                }
            }
        }

        item(key = "footer") {
            Text(
                text = "NekoEdit 基于 miuix 构建，仅用于演示组件用法。",
                style = MiuixTheme.textStyles.footnote2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 20.dp),
            )
        }
    }

    val state = updateState
    OverlayDialog(
        show = state != null,
        title = when (state) {
            is UpdateState.Available -> "发现新版本"
            is UpdateState.UpToDate -> "已是最新版本"
            else -> "检查更新失败"
        },
        summary = when (state) {
            is UpdateState.Available -> "最新版本 v${state.info.version}，当前版本 v${BuildConfig.VERSION_NAME}"
            is UpdateState.UpToDate -> "当前版本 v${BuildConfig.VERSION_NAME} 已经是最新的"
            is UpdateState.Failed -> state.message
            null -> null
        },
        onDismissRequest = { updateState = null },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextButton(
                text = "关闭",
                onClick = { updateState = null },
                modifier = Modifier.weight(1f),
            )
            if (state is UpdateState.Available) {
                TextButton(
                    text = "前往下载",
                    onClick = {
                        uriHandler.openUri(state.info.htmlUrl)
                        updateState = null
                    },
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun AppHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher),
            contentDescription = null,
            modifier = Modifier.size(96.dp),
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "NekoEdit",
            style = MiuixTheme.textStyles.title1,
            color = MiuixTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "用 miuix 拼起来的极简文本编辑器",
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            textAlign = TextAlign.Center,
        )
    }
}
