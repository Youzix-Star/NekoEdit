/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import kotlin.math.roundToInt

val sampleDocument: String = """
    # NekoEdit

    NekoEdit 是一个用 miuix 搭建的小型文本编辑器演示。

    它用到的 miuix 组件：
    - Scaffold / SmallTopAppBar
    - TabRow
    - TextField
    - Card / SmallTitle / HorizontalDivider
    - SwitchPreference / SliderPreference / RadioButtonPreference / ArrowPreference
    - FloatingActionButton / Snackbar / OverlayDialog

    去「设置」页可以切换主题模式、字号以及几个开关项。
""".trimIndent()

@Composable
fun EditorScreen(
    document: String,
    onDocumentChange: (String) -> Unit,
    fontSize: Float,
    onNotify: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineCount = if (document.isEmpty()) 0 else document.lines().size

    LazyColumn(
        modifier = modifier.overScrollVertical(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card {
                TextField(
                    value = document,
                    onValueChange = onDocumentChange,
                    label = "正文",
                    textStyle = MiuixTheme.textStyles.main.copy(fontSize = fontSize.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                )
            }
        }

        item {
            SmallTitle(text = "统计")
            Card {
                StatisticRow(label = "字符数", value = document.length.toString())
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                StatisticRow(label = "行数", value = lineCount.toString())
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                StatisticRow(label = "字号", value = "${fontSize.roundToInt()} sp")
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextButton(
                    text = "清空",
                    onClick = {
                        onDocumentChange("")
                        onNotify("正文已清空")
                    },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = "恢复示例",
                    onClick = {
                        onDocumentChange(sampleDocument)
                        onNotify("已恢复示例正文")
                    },
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun StatisticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MiuixTheme.textStyles.body1)
        Text(
            text = value,
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}
