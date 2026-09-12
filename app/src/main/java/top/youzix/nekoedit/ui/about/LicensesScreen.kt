/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit.ui.about

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.utils.overScrollVertical

private data class LicenseEntry(
    val name: String,
    val license: String,
    val url: String,
)

private val licenses = listOf(
    LicenseEntry("miuix", "Apache License 2.0", "https://github.com/compose-miuix-ui/miuix"),
    LicenseEntry("AndroidX / Jetpack Compose", "Apache License 2.0", "https://cs.android.com/androidx/platform/frameworks/support"),
    LicenseEntry("Material Icons Extended", "Apache License 2.0", "https://fonts.google.com/icons"),
    LicenseEntry("AndroidLiquidGlass (Kyant0)", "Apache License 2.0", "https://github.com/Kyant0/AndroidLiquidGlass"),
    LicenseEntry("Kotlin / kotlinx.coroutines", "Apache License 2.0", "https://github.com/JetBrains/kotlin"),
    LicenseEntry("Material Color Utilities", "Apache License 2.0", "https://github.com/material-foundation/material-color-utilities"),
)

@Composable
fun LicensesScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = modifier.overScrollVertical(),
        contentPadding = contentPadding,
    ) {
        item(key = "licenses") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
            ) {
                licenses.forEachIndexed { index, entry ->
                    if (index > 0) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                    ArrowPreference(
                        title = entry.name,
                        summary = entry.license,
                        onClick = { uriHandler.openUri(entry.url) },
                    )
                }
            }
        }
    }
}
