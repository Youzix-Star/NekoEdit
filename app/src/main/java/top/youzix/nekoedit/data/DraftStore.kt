/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONObject

/**
 * Tiny process-wide draft store backed by [SharedPreferences].
 *
 * Mutations touch the in-memory [drafts] list so the UI updates immediately; writing to disk is
 * explicit through [persist] so typing does not hit storage on every keystroke.
 */
object DraftStore {
    private const val PREFS_NAME = "nekoedit.drafts"
    private const val KEY_DRAFTS = "drafts"

    val drafts = mutableStateListOf<Draft>()

    private var loaded = false
    private var dirty = false

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun ensureLoaded(context: Context) {
        if (loaded) return
        loaded = true
        val raw = prefs(context).getString(KEY_DRAFTS, null)
        if (raw == null) {
            drafts.addAll(sampleDrafts())
            dirty = true
            persist(context)
            return
        }
        val parsed = runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(
                        Draft(
                            id = item.optString("id", java.util.UUID.randomUUID().toString()),
                            title = item.optString("title"),
                            content = item.optString("content"),
                            updatedAt = item.optLong("updatedAt", System.currentTimeMillis()),
                        ),
                    )
                }
            }
        }
        parsed.onSuccess { drafts.addAll(it) }
            .onFailure { drafts.addAll(sampleDrafts()) }
    }

    fun create(context: Context): Draft {
        val draft = Draft()
        drafts.add(0, draft)
        dirty = true
        persist(context)
        return draft
    }

    fun find(id: String?): Draft? = id?.let { wanted -> drafts.firstOrNull { it.id == wanted } }

    /** Replaces the body of [id] in memory; persistence is deferred to [persist]. */
    fun updateContent(id: String, content: String) {
        val index = drafts.indexOfFirst { it.id == id }
        if (index < 0) return
        drafts[index] = drafts[index].copy(content = content).touch()
        dirty = true
    }

    fun updateTitle(id: String, title: String) {
        val index = drafts.indexOfFirst { it.id == id }
        if (index < 0) return
        drafts[index] = drafts[index].copy(title = title).touch()
        dirty = true
    }

    fun delete(context: Context, id: String) {
        drafts.removeAll { it.id == id }
        dirty = true
        persist(context)
    }

    fun persist(context: Context) {
        if (!dirty) return
        dirty = false
        val array = JSONArray()
        drafts.forEach { draft ->
            array.put(
                JSONObject().apply {
                    put("id", draft.id)
                    put("title", draft.title)
                    put("content", draft.content)
                    put("updatedAt", draft.updatedAt)
                },
            )
        }
        prefs(context).edit().putString(KEY_DRAFTS, array.toString()).apply()
    }

    private fun sampleDrafts(): List<Draft> = listOf(
        Draft(
            title = "欢迎使用 NekoEdit",
            content = """
                # 欢迎使用 NekoEdit

                NekoEdit 是一个用 miuix 搭建的 Compose 示例应用。

                - 底部的液态玻璃悬浮底盘可以在四个页面之间切换
                - 「编辑器」页的文字会实时保存到草稿列表
                - 「设置」页可以切换主题、动态取色和字号
                - 「关于」页可以查看源码、开源许可并检查更新

                试着修改这段文字，然后回到「主页」看看效果。
            """.trimIndent(),
            updatedAt = System.currentTimeMillis(),
        ),
        Draft(
            title = "miuix 组件清单",
            content = """
                miuix 组件清单

                Scaffold / SmallTopAppBar / TabRow
                Card / SmallTitle / HorizontalDivider
                TextField / Switch / Slider
                FloatingActionButton / Snackbar / OverlayDialog
                SwitchPreference / SliderPreference / RadioButtonPreference / ArrowPreference
            """.trimIndent(),
            updatedAt = System.currentTimeMillis() - 3_600_000L,
        ),
    )
}
