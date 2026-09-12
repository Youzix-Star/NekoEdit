/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit.data

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

private val idCounter = AtomicLong()

data class Draft(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
) {
    /** [title] when the user set one, otherwise the first non-blank line of the body. */
    val displayTitle: String
        get() {
            val explicit = title.trim()
            if (explicit.isNotEmpty()) return explicit
            return content.lineSequence()
                .firstOrNull { it.isNotBlank() }
                ?.trim()
                ?.take(24)
                .orEmpty()
                .ifBlank { "空白草稿" }
        }

    /** Second and later non-blank lines, used as the list summary. */
    val preview: String
        get() = content.lineSequence()
            .filter { it.isNotBlank() }
            .drop(1)
            .joinToString(" ")
            .trim()
            .take(60)

    val lineCount: Int get() = if (content.isEmpty()) 0 else content.lines().size

    val wordCount: Int get() = content.count { !it.isWhitespace() }

    fun touch(): Draft = copy(updatedAt = System.currentTimeMillis() + idCounter.incrementAndGet())
}
