/*
 * Copyright 2026, Youzix-Star
 * SPDX-License-Identifier: LGPL-2.1
 */

package top.youzix.nekoedit.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.ui.graphics.vector.ImageVector

/** Material icons used by NekoEdit. */
object AppIcons {
    val Home: ImageVector = Icons.Rounded.Home
    val Editor: ImageVector = Icons.Rounded.Edit
    val Settings: ImageVector = Icons.Rounded.Settings
    val About: ImageVector = Icons.Rounded.Info

    val Add: ImageVector = Icons.Rounded.Add
    val Draft: ImageVector = Icons.AutoMirrored.Rounded.Article
    val Save: ImageVector = Icons.Rounded.Save
    val Copy: ImageVector = Icons.Rounded.ContentCopy
    val Clear: ImageVector = Icons.Rounded.DeleteSweep
    val Delete: ImageVector = Icons.Rounded.DeleteOutline
    val Check: ImageVector = Icons.Rounded.Check
    val Back: ImageVector = Icons.AutoMirrored.Rounded.ArrowBack

    val SourceCode: ImageVector = Icons.Rounded.Code
    val License: ImageVector = Icons.Rounded.Description
    val Update: ImageVector = Icons.Rounded.SystemUpdate
    val Refresh: ImageVector = Icons.Rounded.Refresh
    val Developer: ImageVector = Icons.Rounded.Person
    val Feedback: ImageVector = Icons.Rounded.Email
}
