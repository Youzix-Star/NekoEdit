/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit.ui.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.preference.RadioButtonPreference
import top.yukonga.miuix.kmp.preference.SliderPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.youzix.nekoedit.ui.ThemeModeOptions
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    colorSchemeMode: ColorSchemeMode,
    onColorSchemeModeChange: (ColorSchemeMode) -> Unit,
    useLiquidGlass: Boolean,
    onUseLiquidGlassChange: (Boolean) -> Unit,
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    autoSave: Boolean,
    onAutoSaveChange: (Boolean) -> Unit,
    showStatistics: Boolean,
    onShowStatisticsChange: (Boolean) -> Unit,
    topPadding: Dp,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.overScrollVertical(),
        contentPadding = PaddingValues(top = topPadding, bottom = bottomPadding + 24.dp),
    ) {
        item(key = "appearance") {
            SmallTitle(text = "外观")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                ThemeModeOptions.forEachIndexed { index, (mode, label) ->
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

        item(key = "interface") {
            SmallTitle(text = "界面")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                SwitchPreference(
                    title = "液态玻璃底栏",
                    summary = "底部导航使用 iOS 风格的实时毛玻璃与高光；关闭后变为不透明悬浮样式",
                    checked = useLiquidGlass,
                    onCheckedChange = onUseLiquidGlassChange,
                )
            }
        }

        item(key = "editor") {
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
                    summary = "离开编辑页时自动写入草稿",
                    checked = autoSave,
                    onCheckedChange = onAutoSaveChange,
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SwitchPreference(
                    title = "显示统计卡片",
                    summary = "在编辑页显示字符数、字数和行数",
                    checked = showStatistics,
                    onCheckedChange = onShowStatisticsChange,
                )
            }
        }
    }
}
