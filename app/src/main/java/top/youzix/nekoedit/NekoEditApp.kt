/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.Ok
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import top.youzix.nekoedit.ui.AboutDialog
import top.youzix.nekoedit.ui.EditorScreen
import top.youzix.nekoedit.ui.SettingsScreen
import top.youzix.nekoedit.ui.sampleDocument

/**
 * Entry point of the application.
 *
 * [ThemeController] drives every Miuix colour role, so switching the mode here restyles
 * the whole tree without touching any component.
 */
@Composable
fun NekoEditApp() {
    var colorSchemeMode by remember { mutableStateOf(ColorSchemeMode.System) }
    val themeController = remember(colorSchemeMode) {
        ThemeController(colorSchemeMode = colorSchemeMode)
    }

    MiuixTheme(controller = themeController) {
        AppScaffold(
            colorSchemeMode = colorSchemeMode,
            onColorSchemeModeChange = { colorSchemeMode = it },
        )
    }
}

@Composable
private fun AppScaffold(
    colorSchemeMode: ColorSchemeMode,
    onColorSchemeModeChange: (ColorSchemeMode) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showAboutDialog by remember { mutableStateOf(false) }

    var document by remember { mutableStateOf(sampleDocument) }
    var fontSize by remember { mutableFloatStateOf(16f) }
    var autoSave by remember { mutableStateOf(true) }
    var showLineNumbers by remember { mutableStateOf(false) }

    val tabs = remember { listOf("编辑", "设置") }
    val notify: (String) -> Unit = { message ->
        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = "NekoEdit",
                subtitle = tabs[selectedTab],
                actions = {
                    IconButton(onClick = { showAboutDialog = true }) {
                        Icon(imageVector = MiuixIcons.Info, contentDescription = "关于")
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(state = snackbarHostState)
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = { notify("草稿已保存（${document.length} 字符）") }) {
                    Icon(imageVector = MiuixIcons.Ok, contentDescription = "保存")
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            TabRow(
                tabs = tabs,
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )

            when (selectedTab) {
                0 -> EditorScreen(
                    document = document,
                    onDocumentChange = { document = it },
                    fontSize = fontSize,
                    onNotify = notify,
                    modifier = Modifier.fillMaxSize(),
                )

                else -> SettingsScreen(
                    colorSchemeMode = colorSchemeMode,
                    onColorSchemeModeChange = onColorSchemeModeChange,
                    fontSize = fontSize,
                    onFontSizeChange = { fontSize = it },
                    autoSave = autoSave,
                    onAutoSaveChange = { autoSave = it },
                    showLineNumbers = showLineNumbers,
                    onShowLineNumbersChange = { showLineNumbers = it },
                    onOpenAbout = { showAboutDialog = true },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // Rendered through the Scaffold's popup host, so it must stay inside this scope.
        AboutDialog(show = showAboutDialog, onDismiss = { showAboutDialog = false })
    }
}
