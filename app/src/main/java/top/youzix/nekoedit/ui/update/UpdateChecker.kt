/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit.ui.update

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class ReleaseInfo(
    val tagName: String,
    val name: String,
    val body: String,
    val htmlUrl: String,
    val publishedAt: String,
) {
    /** `v1.2.3` / `1.2.3` -> `1.2.3` */
    val version: String get() = tagName.trim().removePrefix("v").removePrefix("V")
}

/** Reads the newest release straight from the GitHub Releases API. */
object UpdateChecker {
    private const val LATEST_RELEASE_API =
        "https://api.github.com/repos/Youzix-Star/NekoEdit/releases/latest"

    suspend fun fetchLatest(): Result<ReleaseInfo> = withContext(Dispatchers.IO) {
        runCatching {
            val connection = (URL(LATEST_RELEASE_API).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "NekoEdit")
            }
            try {
                val code = connection.responseCode
                if (code !in 200..299) error("GitHub API 返回 HTTP $code")
                val payload = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(payload)
                ReleaseInfo(
                    tagName = json.optString("tag_name"),
                    name = json.optString("name"),
                    body = json.optString("body"),
                    htmlUrl = json.optString("html_url"),
                    publishedAt = json.optString("published_at"),
                )
            } finally {
                connection.disconnect()
            }
        }
    }

    /** True when [latest] is a strictly newer dotted version than [current]. */
    fun isNewer(current: String, latest: String): Boolean {
        val currentParts = current.versionParts()
        val latestParts = latest.versionParts()
        for (index in 0 until maxOf(currentParts.size, latestParts.size)) {
            val left = currentParts.getOrElse(index) { 0 }
            val right = latestParts.getOrElse(index) { 0 }
            if (left != right) return right > left
        }
        return false
    }

    private fun String.versionParts(): List<Int> =
        trim().removePrefix("v").removePrefix("V")
            .substringBefore('-')
            .split('.')
            .mapNotNull { it.trim().toIntOrNull() }
}
