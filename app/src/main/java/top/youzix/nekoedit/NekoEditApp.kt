/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.youzix.nekoedit.data.DraftStore
import top.youzix.nekoedit.ui.AppIcons
import top.youzix.nekoedit.ui.NekoEditTheme
import top.youzix.nekoedit.ui.about.AboutScreen
import top.youzix.nekoedit.ui.about.LicensesScreen
import top.youzix.nekoedit.ui.editor.EditorScreen
import top.youzix.nekoedit.ui.home.HomeScreen
import top.youzix.nekoedit.ui.liquid.FloatingBottomBar
import top.youzix.nekoedit.ui.settings.SettingsScreen

private const val PAGE_HOME = 0
private const val PAGE_EDITOR = 1
private const val PAGE_SETTINGS = 2
private const val PAGE_ABOUT = 3

@Composable
fun NekoEditApp() {
    var colorSchemeMode by remember { mutableStateOf(ColorSchemeMode.System) }
    var useLiquidGlass by remember { mutableStateOf(true) }
    var fontSize by remember { mutableFloatStateOf(16f) }
    var autoSave by remember { mutableStateOf(true) }
    var showStatistics by remember { mutableStateOf(true) }

    NekoEditTheme(colorSchemeMode = colorSchemeMode) {
        MainScreen(
            colorSchemeMode = colorSchemeMode,
            onColorSchemeModeChange = { colorSchemeMode = it },
            useLiquidGlass = useLiquidGlass,
            onUseLiquidGlassChange = { useLiquidGlass = it },
            fontSize = fontSize,
            onFontSizeChange = { fontSize = it },
            autoSave = autoSave,
            onAutoSaveChange = { autoSave = it },
            showStatistics = showStatistics,
            onShowStatisticsChange = { showStatistics = it },
        )
    }
}

@Composable
private fun MainScreen(
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
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { DraftStore.ensureLoaded(context) }

    val drafts = DraftStore.drafts
    var selectedDraftId by remember { mutableStateOf<String?>(null) }
    var showLicenses by remember { mutableStateOf(false) }
    val currentDraft = DraftStore.find(selectedDraftId) ?: drafts.firstOrNull()

    val navigationItems = remember {
        listOf(
            NavigationItem(label = "主页", icon = AppIcons.Home),
            NavigationItem(label = "编辑器", icon = AppIcons.Editor),
            NavigationItem(label = "设置", icon = AppIcons.Settings),
            NavigationItem(label = "关于", icon = AppIcons.About),
        )
    }
    val pagerState = rememberPagerState(pageCount = { navigationItems.size })
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val notify: (String) -> Unit = { message ->
        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
    }

    val surfaceColor = MiuixTheme.colorScheme.surface
    val backdrop = rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }

    val openDraft: (String) -> Unit = { id ->
        selectedDraftId = id
        coroutineScope.launch { pagerState.animateScrollToPage(PAGE_EDITOR) }
    }
    val createDraft: () -> Unit = {
        val draft = DraftStore.create(context)
        notify("已新建草稿")
        openDraft(draft.id)
    }

    // Write pending edits to disk whenever the editor page is left behind.
    val currentPage = pagerState.currentPage
    LaunchedEffect(currentPage, autoSave) {
        if (currentPage != PAGE_EDITOR && autoSave) DraftStore.persist(context)
    }
    LaunchedEffect(drafts.size, selectedDraftId) {
        if (selectedDraftId == null && drafts.isNotEmpty()) selectedDraftId = drafts.first().id
    }

    // ---- predictive back ----
    // The progress Animatable is read by the graphicsLayer below, so the screen follows the
    // gesture, settles forward on commit and springs back when the gesture is cancelled.
    val backProgress = remember { Animatable(0f) }
    var backSwipeEdge by remember { mutableIntStateOf(BackEventCompat.EDGE_LEFT) }
    val settleScope = rememberCoroutineScope()
    val canGoBack = showLicenses || currentPage != PAGE_HOME

    PredictiveBackHandler(enabled = canGoBack) { progress ->
        try {
            progress.collect { event ->
                backSwipeEdge = event.swipeEdge
                backProgress.snapTo(event.progress)
            }
            // Committed: finish collapsing, swap the content while it is hidden, then expand.
            settleScope.launch {
                backProgress.animateTo(1f, tween(durationMillis = 140))
                if (showLicenses) {
                    showLicenses = false
                } else {
                    pagerState.scrollToPage(PAGE_HOME)
                }
                backProgress.animateTo(0f, tween(durationMillis = 220))
            }
        } catch (cancelled: CancellationException) {
            settleScope.launch {
                backProgress.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
            }
            throw cancelled
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val progress = backProgress.value
                    val scale = 1f - 0.10f * progress
                    scaleX = scale
                    scaleY = scale
                    val drift = 32.dp.toPx() * progress
                    translationX = if (backSwipeEdge == BackEventCompat.EDGE_RIGHT) -drift else drift
                    alpha = 1f - 0.30f * progress
                },
        ) {
                Scaffold(
                    topBar = {
                when (currentPage) {
                    PAGE_HOME -> SmallTopAppBar(title = "NekoEdit", subtitle = "草稿")
                    PAGE_EDITOR -> SmallTopAppBar(
                        title = "编辑器",
                        subtitle = currentDraft?.displayTitle ?: "未选择草稿",
                    )
                    PAGE_SETTINGS -> SmallTopAppBar(title = "设置", subtitle = "主题与编辑器偏好")
                    else -> if (showLicenses) {
                        SmallTopAppBar(
                            title = "开源许可",
                            navigationIcon = {
                                IconButton(onClick = { showLicenses = false }) {
                                    Icon(imageVector = AppIcons.Back, contentDescription = "返回")
                                }
                            },
                        )
                    } else {
                        SmallTopAppBar(title = "关于", subtitle = "v${BuildConfig.VERSION_NAME}")
                    }
                }
            },
            bottomBar = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    FloatingBottomBar(
                        items = navigationItems,
                        selectedIndex = currentPage,
                        onItemClick = { index ->
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        backdrop = backdrop,
                        isBlurActive = useLiquidGlass,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(
                                bottom = 12.dp +
                                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                            ),
                    )
                }
            },
            floatingActionButton = {
                when (currentPage) {
                    PAGE_HOME -> FloatingActionButton(onClick = createDraft) {
                        Icon(imageVector = AppIcons.Add, contentDescription = "新建草稿")
                    }
                    PAGE_EDITOR -> if (currentDraft != null) {
                        FloatingActionButton(
                            onClick = {
                                DraftStore.persist(context)
                                notify("草稿已保存")
                            },
                        ) {
                            Icon(imageVector = AppIcons.Save, contentDescription = "保存草稿")
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(state = snackbarHostState) },
        ) { innerPadding ->
            val layoutDirection = LocalLayoutDirection.current
            // Horizontal padding lives on the list rather than on every card, which is how the
            // reference app lays its pages out.
            val pagePadding = PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection) + 12.dp,
                top = innerPadding.calculateTopPadding() + 12.dp,
                end = innerPadding.calculateEndPadding(layoutDirection) + 12.dp,
                bottom = innerPadding.calculateBottomPadding() + 12.dp,
            )

            // The page content is recorded into `backdrop`, so the floating bar can refract it.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .layerBackdrop(backdrop)
                    .imePadding(),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 1,
                ) { page ->
                    when (page) {
                        PAGE_HOME -> HomeScreen(
                            drafts = drafts,
                            contentPadding = pagePadding,
                            onOpenDraft = { draft -> openDraft(draft.id) },
                            onCreateDraft = createDraft,
                            onDeleteDraft = { draft ->
                                DraftStore.delete(context, draft.id)
                                notify("已删除「${draft.displayTitle}」")
                            },
                            modifier = Modifier.fillMaxSize(),
                        )

                        PAGE_EDITOR -> EditorScreen(
                            draft = currentDraft,
                            fontSize = fontSize,
                            showStatistics = showStatistics,
                            contentPadding = pagePadding,
                            onTitleChange = { title ->
                                currentDraft?.let { DraftStore.updateTitle(it.id, title) }
                            },
                            onContentChange = { content ->
                                currentDraft?.let { DraftStore.updateContent(it.id, content) }
                            },
                            onCreateDraft = createDraft,
                            onNotify = notify,
                            modifier = Modifier.fillMaxSize(),
                        )

                        PAGE_SETTINGS -> SettingsScreen(
                            colorSchemeMode = colorSchemeMode,
                            onColorSchemeModeChange = onColorSchemeModeChange,
                            useLiquidGlass = useLiquidGlass,
                            onUseLiquidGlassChange = onUseLiquidGlassChange,
                            fontSize = fontSize,
                            onFontSizeChange = onFontSizeChange,
                            autoSave = autoSave,
                            onAutoSaveChange = onAutoSaveChange,
                            showStatistics = showStatistics,
                            onShowStatisticsChange = onShowStatisticsChange,
                            contentPadding = pagePadding,
                            modifier = Modifier.fillMaxSize(),
                        )

                        else -> if (showLicenses) {
                            LicensesScreen(
                                contentPadding = pagePadding,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            AboutScreen(
                                contentPadding = pagePadding,
                                onOpenLicenses = { showLicenses = true },
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
        }
    }
}
