package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.components.CashFlowCard
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CategorySpend
import com.example.ui.viewmodel.StatsPeriod
import com.example.ui.viewmodel.WalletViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncomeThisPeriod.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpenseThisPeriod.collectAsStateWithLifecycle()
    val categorySpends by viewModel.categorySpends.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()
    val period by viewModel.statsPeriod.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("screen_statistics"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Statistics",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Financial breakdown & analytics",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // 1. Balance Overview Card
        item {
            OverviewMetricCard(
                title = "Balance & Net Worth",
                mainValue = "${formatAmount(totalBalance)} $currentCurrency",
                icon = Icons.Default.AccountBalanceWallet,
                accentColor = AccentBlue,
                rows = listOf(
                    "Total Assets" to "${formatAmount(totalBalance)} $currentCurrency",
                    "Liabilities" to "0.00 $currentCurrency"
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        // 2. Spending Overview Card
        item {
            val dailyAvg = totalExpense / 30.0
            OverviewMetricCard(
                title = "Spending",
                mainValue = "${formatAmount(totalExpense)} $currentCurrency",
                icon = Icons.Default.TrendingDown,
                accentColor = AccentRed,
                rows = listOf(
                    "Daily Average" to "${formatAmount(dailyAvg)} $currentCurrency",
                    "Active Categories" to "${categorySpends.size} categories"
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        // 3. Cash Flow (with progress bar)
        item {
            CashFlowCard(
                income = totalIncome,
                expense = totalExpense,
                currency = currentCurrency,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        // 4. Outlook Card
        item {
            val netRunRate = totalIncome - totalExpense
            val projected = totalBalance + (netRunRate * 0.5)
            OverviewMetricCard(
                title = "Outlook",
                mainValue = "On Track (${if (netRunRate >= 0) "Surplus" else "Deficit"})",
                icon = Icons.Default.Timeline,
                accentColor = if (netRunRate >= 0) AccentGreen else AccentOrange,
                rows = listOf(
                    "Projected End Balance" to "${formatAmount(projected)} $currentCurrency",
                    "Savings Health" to if (netRunRate >= 0) "Excellent (Positive)" else "Attention Needed"
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        // 5. Credit Card
        item {
            OverviewMetricCard(
                title = "Credit & Debt",
                mainValue = "0.00 $currentCurrency",
                icon = Icons.Default.CreditCard,
                accentColor = AccentPurple,
                rows = listOf(
                    "Credit Utilization" to "0%",
                    "Available Limit" to "100,000.00 $currentCurrency"
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        // 6. Reports & Category Breakdown Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(AccentCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Reports: Spending by Category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (categorySpends.isEmpty()) {
                    Text(
                        text = "No category spending recorded for this period.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                } else {
                    categorySpends.forEach { spend ->
                        CategorySpendBarRow(
                            spend = spend,
                            currency = currentCurrency
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        // 7. Time Selector at the bottom ("This month" with left/right arrows and dots indicator)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            TimeSelectorBottomCard(
                currentPeriod = period,
                onPrev = { viewModel.prevStatsPeriod() },
                onNext = { viewModel.nextStatsPeriod() },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun OverviewMetricCard(
    title: String,
    mainValue: String,
    icon: ImageVector,
    accentColor: Color,
    rows: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = mainValue,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        rows.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun CategorySpendBarRow(
    spend: CategorySpend,
    currency: String
) {
    val catColor = Color(spend.category.colorHex)
    val animatedPercent by animateFloatAsState(
        targetValue = spend.percentage,
        animationSpec = tween(600),
        label = "cat_spend_pct"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(catColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = spend.category.name,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "${formatAmount(spend.totalSpent)} $currency (${(spend.percentage * 100).toInt()}%)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(DarkSurfaceElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedPercent)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(catColor)
            )
        }
    }
}

@Composable
private fun TimeSelectorBottomCard(
    currentPeriod: StatsPeriod,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrev, modifier = Modifier.testTag("btn_stats_prev")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Period",
                    tint = TextPrimary
                )
            }

            Text(
                text = currentPeriod.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.testTag("txt_stats_period")
            )

            IconButton(onClick = onNext, modifier = Modifier.testTag("btn_stats_next")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Period",
                    tint = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Dots Indicator for periods (Past Month, This Month, This Year)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatsPeriod.values().forEach { p ->
                val isSelected = p == currentPeriod
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) AccentBlue else DarkSurfaceElevated)
                )
            }
        }
    }
}

private fun formatAmount(amount: Double): String {
    return NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(amount)
}
