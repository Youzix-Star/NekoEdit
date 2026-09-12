/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit.ui.editor

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.youzix.nekoedit.data.Draft
import top.youzix.nekoedit.ui.AppIcons
import kotlin.math.roundToInt

@Composable
fun EditorScreen(
    draft: Draft?,
    fontSize: Float,
    showStatistics: Boolean,
    contentPadding: PaddingValues,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onCreateDraft: () -> Unit,
    onNotify: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.overScrollVertical(),
        contentPadding = contentPadding,
    ) {
        if (draft == null) {
            item(key = "empty") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "还没有选中的草稿",
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "新建一条，或者回主页挑一条已有的",
                            style = MiuixTheme.textStyles.footnote2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onCreateDraft,
                            colors = ButtonDefaults.buttonColorsPrimary(),
                        ) {
                            Icon(imageVector = AppIcons.Add, contentDescription = null)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(text = "新建草稿")
                        }
                    }
                }
            }
        } else {
            item(key = "title") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextField(
                        value = draft.title,
                        onValueChange = onTitleChange,
                        label = "标题",
                        singleLine = true,
                        useLabelAsPlaceholder = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item(key = "content") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextField(
                        value = draft.content,
                        onValueChange = onContentChange,
                        label = "正文",
                        textStyle = MiuixTheme.textStyles.main.copy(fontSize = fontSize.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                    )
                }
            }

            item(key = "actions") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextButton(
                        text = "清空正文",
                        onClick = {
                            onContentChange("")
                            onNotify("正文已清空")
                        },
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        text = "保存草稿",
                        onClick = { onNotify("草稿已保存") },
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (showStatistics) {
                item(key = "statistics") {
                    SmallTitle(text = "统计")
                    Card(modifier = Modifier.fillMaxWidth()) {
                        StatisticRow(label = "字符数（含空格）", value = draft.content.length.toString())
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                        StatisticRow(label = "字数（不含空白）", value = draft.wordCount.toString())
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                        StatisticRow(label = "行数", value = draft.lineCount.toString())
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                        StatisticRow(label = "字号", value = "${fontSize.roundToInt()} sp")
                    }
                }
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
