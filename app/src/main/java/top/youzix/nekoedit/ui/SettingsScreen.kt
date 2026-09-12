/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.RadioButtonPreference
import top.yukonga.miuix.kmp.preference.SliderPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.utils.overScrollVertical
import kotlin.math.roundToInt

private val themeModeOptions = listOf(
    ColorSchemeMode.System to "跟随系统",
    ColorSchemeMode.Light to "浅色",
    ColorSchemeMode.Dark to "深色",
)

@Composable
fun SettingsScreen(
    colorSchemeMode: ColorSchemeMode,
    onColorSchemeModeChange: (ColorSchemeMode) -> Unit,
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    autoSave: Boolean,
    onAutoSaveChange: (Boolean) -> Unit,
    showLineNumbers: Boolean,
    onShowLineNumbersChange: (Boolean) -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.overScrollVertical(),
        contentPadding = PaddingValues(bottom = 104.dp),
    ) {
        item {
            SmallTitle(text = "外观")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                themeModeOptions.forEachIndexed { index, (mode, label) ->
                    if (index > 0) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                    RadioButtonPreference(
                        title = label,
                        selected = colorSchemeMode == mode,
                        onClick = { onColorSchemeModeChange(mode) },
                    )
                }
            }
        }

        item {
            SmallTitle(text = "编辑器")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                SliderPreference(
                    value = fontSize,
                    onValueChange = onFontSizeChange,
                    title = "字号",
                    valueText = "${fontSize.roundToInt()} sp",
                    valueRange = 12f..28f,
                    steps = 15,
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SwitchPreference(
                    title = "自动保存",
                    summary = "退出编辑页时自动保存草稿",
                    checked = autoSave,
                    onCheckedChange = onAutoSaveChange,
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SwitchPreference(
                    title = "显示行号",
                    summary = "在编辑区域左侧显示行号",
                    checked = showLineNumbers,
                    onCheckedChange = onShowLineNumbersChange,
                )
            }
        }

        item {
            SmallTitle(text = "关于")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                ArrowPreference(
                    title = "关于 NekoEdit",
                    summary = "使用 miuix 构建的 Compose 示例应用",
                    onClick = onOpenAbout,
                )
            }
        }
    }
}
