/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit.ui.home

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.youzix.nekoedit.data.Draft
import top.youzix.nekoedit.ui.AppIcons
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    drafts: List<Draft>,
    topPadding: Dp,
    bottomPadding: Dp,
    onOpenDraft: (Draft) -> Unit,
    onCreateDraft: () -> Unit,
    onDeleteDraft: (Draft) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.overScrollVertical(),
        contentPadding = PaddingValues(top = topPadding, bottom = bottomPadding + 24.dp),
    ) {
        item(key = "overview") {
            OverviewCard(
                drafts = drafts,
                onCreateDraft = onCreateDraft,
            )
        }

        item(key = "drafts") {
            SmallTitle(text = "全部草稿")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                if (drafts.isEmpty()) {
                    EmptyDrafts(onCreateDraft = onCreateDraft)
                } else {
                    drafts.forEachIndexed { index, draft ->
                        if (index > 0) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                        DraftRow(
                            draft = draft,
                            onOpen = { onOpenDraft(draft) },
                            onDelete = { onDeleteDraft(draft) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewCard(
    drafts: List<Draft>,
    onCreateDraft: () -> Unit,
) {
    val totalWords = drafts.sumOf { it.wordCount }
    val lastEdited = drafts.maxOfOrNull { it.updatedAt }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
        Text(
            text = "NekoEdit",
            style = MiuixTheme.textStyles.title3,
            color = MiuixTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "把想法随手写下来的小编辑器",
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Statistic(label = "草稿", value = drafts.size.toString())
            Statistic(label = "总字数", value = totalWords.toString())
            Statistic(
                label = "最近编辑",
                value = lastEdited?.let { formatTimestamp(it) } ?: "—",
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCreateDraft,
            colors = ButtonDefaults.buttonColorsPrimary(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(imageVector = AppIcons.Add, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "新建草稿")
        }
    }
}

@Composable
private fun Statistic(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MiuixTheme.textStyles.title3,
            color = MiuixTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

@Composable
private fun DraftRow(
    draft: Draft,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    ArrowPreference(
        title = draft.displayTitle,
        summary = draft.preview.ifBlank { "${draft.lineCount} 行 · ${draft.wordCount} 字" },
        startAction = {
            Icon(
                imageVector = AppIcons.Draft,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )
        },
        endActions = {
            IconButton(onClick = onDelete) {
                Icon(imageVector = AppIcons.Delete, contentDescription = "删除草稿")
            }
        },
        onClick = onOpen,
    )
}

@Composable
private fun EmptyDrafts(onCreateDraft: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "还没有草稿",
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "点下面的按钮写第一条",
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onCreateDraft) {
            Icon(imageVector = AppIcons.Add, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "新建草稿")
        }
    }
}

private val timestampFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

private fun formatTimestamp(timestamp: Long): String = timestampFormat.format(Date(timestamp))
