package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.WalletViewModel

data class MoreGridItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val badge: String? = null
)

@Composable
fun MoreScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()
    var infoDialogTitle by remember { mutableStateOf<String?>(null) }
    var infoDialogBody by remember { mutableStateOf<String?>(null) }

    // 100% Free feature grid - No paywalls or subscriptions
    val gridItems = listOf(
        MoreGridItem(
            id = "savings_goals",
            title = "Savings Goals",
            subtitle = "Milestones & target fund",
            icon = Icons.Default.EmojiEvents,
            iconColor = AccentPurple,
            badge = "FREE"
        ),
        MoreGridItem(
            id = "group_sharing",
            title = "Group Sharing",
            subtitle = "Collaborate with family",
            icon = Icons.Default.Group,
            iconColor = AccentBlue
        ),
        MoreGridItem(
            id = "records",
            title = "Records",
            subtitle = "Full transaction ledger",
            icon = Icons.Default.History,
            iconColor = AccentGreen
        ),
        MoreGridItem(
            id = "investments",
            title = "Investments",
            subtitle = "Portfolio & return tracker",
            icon = Icons.Default.TrendingUp,
            iconColor = AccentCyan
        ),
        MoreGridItem(
            id = "business",
            title = "Wallet for Business",
            subtitle = "Corporate expense ledger",
            icon = Icons.Default.BusinessCenter,
            iconColor = AccentOrange
        ),
        MoreGridItem(
            id = "share_cost",
            title = "ShareCost",
            subtitle = "Interactive bill splitter",
            icon = Icons.Default.CallSplit,
            iconColor = AccentPink
        ),
        MoreGridItem(
            id = "follow_us",
            title = "Community",
            subtitle = "Tips & release updates",
            icon = Icons.Default.Public,
            iconColor = AccentYellow
        ),
        MoreGridItem(
            id = "settings",
            title = "Settings",
            subtitle = "Currency, accounts, rules",
            icon = Icons.Default.Settings,
            iconColor = TextPrimary
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("screen_more"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_wallet_app_icon_1792047262297),
                    contentDescription = "Wallet Logo",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "More",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "100% Free personal finance tools & settings",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // 2-Column Grid of 8 Functional Square Cards
        items(gridItems) { item ->
            MoreSquareCard(
                item = item,
                onClick = {
                    when (item.id) {
                        "savings_goals" -> viewModel.showSavingsGoalsDialog.value = true
                        "records" -> viewModel.showRecordsDialog.value = true
                        "share_cost" -> viewModel.showShareCostDialog.value = true
                        "settings" -> viewModel.showSettingsDialog.value = true
                        "group_sharing" -> {
                            infoDialogTitle = "Group & Family Sharing"
                            infoDialogBody = "Share household expenses, manage joint groceries, and sync with family members offline or locally."
                        }
                        "investments" -> {
                            infoDialogTitle = "Investments & Portfolio"
                            infoDialogBody = "Monitor stock purchases, mutual fund dividends, and crypto assets alongside your daily cash balances."
                        }
                        "business" -> {
                            infoDialogTitle = "Wallet for Business"
                            infoDialogBody = "Separate company expenses from personal accounts, tag client reimbursements, and export tax summaries."
                        }
                        "follow_us" -> {
                            infoDialogTitle = "Community & Open Support"
                            infoDialogBody = "Wallet is 100% free with no hidden fees or paywalls. All database features run directly on your device."
                        }
                    }
                }
            )
        }
    }

    if (infoDialogTitle != null) {
        AlertDialog(
            onDismissRequest = { infoDialogTitle = null },
            containerColor = DarkSurface,
            title = {
                Text(text = infoDialogTitle ?: "", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = infoDialogBody ?: "", color = TextSecondary, fontSize = 14.sp)
            },
            confirmButton = {
                Button(
                    onClick = { infoDialogTitle = null },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Got It", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun MoreSquareCard(
    item: MoreGridItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("more_card_${item.id}")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(item.iconColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (item.badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(item.iconColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.badge,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Column {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
        }
    }
}
