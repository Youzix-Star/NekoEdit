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
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.youzix.nekoedit.data.Draft
import top.youzix.nekoedit.ui.AppIcons
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    drafts: List<Draft>,
    contentPadding: PaddingValues,
    onOpenDraft: (Draft) -> Unit,
    onCreateDraft: () -> Unit,
    onDeleteDraft: (Draft) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.overScrollVertical(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "status") {
            StatusSection(
                drafts = drafts,
                onCreateDraft = onCreateDraft,
            )
        }

        item(key = "drafts") {
            Column {
                SmallTitle(text = "全部草稿")
                Card(modifier = Modifier.fillMaxWidth()) {
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
}

/** Big call-to-action card plus the numeric overview, mirroring the reference home screen. */
@Composable
private fun StatusSection(
    drafts: List<Draft>,
    onCreateDraft: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            // CardDefaults.InsideMargin is 0.dp, so cards holding raw content must pad themselves.
            insideMargin = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            colors = CardDefaults.defaultColors(
                color = MiuixTheme.colorScheme.primaryContainer,
                contentColor = MiuixTheme.colorScheme.onPrimaryContainer,
            ),
            onClick = onCreateDraft,
            showIndication = true,
            pressFeedbackType = PressFeedbackType.Tilt,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "开始写作",
                        style = MiuixTheme.textStyles.title3,
                        color = MiuixTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "新建一条草稿，随手记下点什么",
                        style = MiuixTheme.textStyles.footnote2,
                        color = MiuixTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                    )
                }
                Spacer(modifier = Modifier.size(12.dp))
                Icon(
                    imageVector = AppIcons.Add,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = MiuixTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatisticCard(
                title = "草稿",
                value = drafts.size.toString(),
                modifier = Modifier.weight(1f),
            )
            StatisticCard(
                title = "总字数",
                value = drafts.sumOf { it.wordCount }.toString(),
                modifier = Modifier.weight(1f),
            )
            StatisticCard(
                title = "总行数",
                value = drafts.sumOf { it.lineCount }.toString(),
                modifier = Modifier.weight(1f),
            )
        }

        val latest = drafts.maxByOrNull { it.updatedAt }
        if (latest != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                BasicComponent(
                    title = "最近编辑",
                    summary = "${latest.displayTitle} · ${formatTimestamp(latest.updatedAt)}",
                )
            }
        }
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = title,
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MiuixTheme.textStyles.title2,
            color = MiuixTheme.colorScheme.onSurface,
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
            text = "点上面的「开始写作」写第一条",
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            textAlign = TextAlign.Center,
        )
    }
}

private val timestampFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

private fun formatTimestamp(timestamp: Long): String = timestampFormat.format(Date(timestamp))
