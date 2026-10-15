package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppTab

@Composable
fun WalletBottomNavBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = DarkBorder, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = DarkSurface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Tab 1: Dashboard
            NavTabItem(
                label = "Dashboard",
                selected = selectedTab == AppTab.DASHBOARD,
                iconSelected = Icons.Filled.Dashboard,
                iconUnselected = Icons.Outlined.Dashboard,
                testTag = "nav_dashboard",
                onClick = { onTabSelected(AppTab.DASHBOARD) }
            )

            // Tab 2: Planning
            NavTabItem(
                label = "Planning",
                selected = selectedTab == AppTab.PLANNING,
                iconSelected = Icons.Filled.CalendarMonth,
                iconUnselected = Icons.Outlined.CalendarMonth,
                testTag = "nav_planning",
                onClick = { onTabSelected(AppTab.PLANNING) }
            )

            // Center Tab: Add Record Button
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(AccentBlue, AccentGreen)
                        )
                    )
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .clickable(onClick = onAddClick)
                    .testTag("nav_add_record"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Record",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Tab 4: Statistics
            NavTabItem(
                label = "Statistics",
                selected = selectedTab == AppTab.STATISTICS,
                iconSelected = Icons.Filled.Insights,
                iconUnselected = Icons.Outlined.Insights,
                testTag = "nav_statistics",
                onClick = { onTabSelected(AppTab.STATISTICS) }
            )

            // Tab 5: More
            NavTabItem(
                label = "More",
                selected = selectedTab == AppTab.MORE,
                iconSelected = Icons.Filled.GridView,
                iconUnselected = Icons.Outlined.GridView,
                testTag = "nav_more",
                onClick = { onTabSelected(AppTab.MORE) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    selected: Boolean,
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    val iconTint = animateColorAsState(
        targetValue = if (selected) AccentBlue else TextSecondary,
        label = "nav_icon_color"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) iconSelected else iconUnselected,
            contentDescription = label,
            tint = iconTint.value,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = iconTint.value,
            fontSize = 11.sp,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
