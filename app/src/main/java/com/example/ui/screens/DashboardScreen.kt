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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
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
import com.example.data.model.Account
import com.example.data.model.Category
import com.example.data.model.Transaction
import com.example.ui.components.BalanceTrendChart
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.WalletViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()
    val trendPoints by viewModel.balanceTrendPoints.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncomeThisPeriod.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpenseThisPeriod.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("screen_dashboard"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Bar: "Dashboard", Notification bell, Settings
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_wallet_app_icon_1792047262297),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Dashboard",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Real-time personal finance overview",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.showNotificationsDialog.value = true },
                        modifier = Modifier.testTag("btn_notifications")
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AccentRed)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.showSettingsDialog.value = true },
                        modifier = Modifier.testTag("btn_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextPrimary
                        )
                    }
                }
            }
        }

        // Row of Account Cards: Card 1 (Cash Account), Card 2 ("Add account" with blue "+" button)
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Existing real accounts stored in Room DB
                items(accounts) { account ->
                    AccountCard(
                        account = account,
                        currency = currentCurrency,
                        modifier = Modifier.width(200.dp)
                    )
                }

                // Card 2: "Add account" card with blue "+" button (opens live form)
                item {
                    AddAccountCard(
                        onClick = { viewModel.showAddAccountDialog.value = true },
                        modifier = Modifier.width(170.dp)
                    )
                }
            }
        }

        // Middle: Live Financial Monthly Cash Summary Card (100% Free, NO subscriptions)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            MonthlyCashSummaryCard(
                income = totalIncome,
                expense = totalExpense,
                currency = currentCurrency,
                onAddRecord = { viewModel.showAddRecordSheet.value = true },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // Bottom: "Balance Trend" section with interactive line chart automatically computed from database
        item {
            Spacer(modifier = Modifier.height(18.dp))
            BalanceTrendChart(
                points = trendPoints,
                currency = currentCurrency,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // Recent Records Section
        item {
            Spacer(modifier = Modifier.height(22.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Records",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${transactions.size} records in database",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No records yet. Tap + to add an expense or income!",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(transactions.take(10)) { tx ->
                val category = categories.find { it.id == tx.categoryId }
                val account = accounts.find { it.id == tx.accountId }
                TransactionItemRow(
                    transaction = tx,
                    category = category,
                    account = account,
                    currency = currentCurrency,
                    onDelete = { viewModel.deleteTransaction(tx) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun AccountCard(
    account: Account,
    currency: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(130.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("account_card_${account.name.lowercase()}")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(account.colorHex).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getAccountIcon(account.icon),
                            contentDescription = null,
                            tint = Color(account.colorHex),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = account.name.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                )
            }

            Column {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = "${formatCurrency(account.balance)}${account.currency}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun AddAccountCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(130.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurfaceElevated.copy(alpha = 0.5f))
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("btn_add_account_card"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(AccentBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add account",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Add account",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun MonthlyCashSummaryCard(
    income: Double,
    expense: Double,
    currency: String,
    onAddRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    val netSavings = income - expense
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
            Text(
                text = "This Month's Cash Flow",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (netSavings >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (netSavings >= 0) AccentGreen else AccentRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (netSavings >= 0) "Surplus" else "Deficit",
                    color = if (netSavings >= 0) AccentGreen else AccentRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Total Income", color = TextSecondary, fontSize = 12.sp)
                Text(
                    text = "+${formatCurrency(income)}$currency",
                    color = AccentGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Column {
                Text(text = "Total Expenses", color = TextSecondary, fontSize = 12.sp)
                Text(
                    text = "-${formatCurrency(expense)}$currency",
                    color = AccentRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Net Saved", color = TextSecondary, fontSize = 12.sp)
                Text(
                    text = "${if (netSavings > 0) "+" else ""}${formatCurrency(netSavings)}$currency",
                    color = if (netSavings >= 0) AccentGreen else AccentRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun TransactionItemRow(
    transaction: Transaction,
    category: Category?,
    account: Account?,
    currency: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpense = transaction.type == "EXPENSE"
    val isIncome = transaction.type == "INCOME"
    val amountColor = when {
        isExpense -> AccentRed
        isIncome -> AccentGreen
        else -> AccentBlue
    }
    val prefix = when {
        isExpense -> "-"
        isIncome -> "+"
        else -> "⇄"
    }

    val catColor = category?.colorHex?.let { Color(it) } ?: AccentBlue
    val icon = getCategoryIcon(category?.icon ?: "receipt")

    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(transaction.dateTime))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(0.5.dp, DarkBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(catColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = catColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = category?.name ?: transaction.type,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
                Text(
                    text = "${account?.name ?: "Account"} • $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                if (transaction.notes.isNotBlank()) {
                    Text(
                        text = transaction.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$prefix${formatCurrency(transaction.amount)} $currency",
                fontWeight = FontWeight.Bold,
                color = amountColor,
                fontSize = 15.sp
            )

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = TextSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

fun getAccountIcon(icon: String): ImageVector {
    return when (icon.lowercase()) {
        "cash" -> Icons.Default.Payments
        "bank" -> Icons.Default.AccountBalance
        "savings" -> Icons.Default.Savings
        "card" -> Icons.Default.CreditCard
        else -> Icons.Default.Payments
    }
}

fun getCategoryIcon(icon: String): ImageVector {
    return when (icon.lowercase()) {
        "restaurant" -> Icons.Default.Restaurant
        "shopping_bag" -> Icons.Default.ShoppingBag
        "home" -> Icons.Default.Home
        "directions_car" -> Icons.Default.DirectionsCar
        "sports_esports" -> Icons.Default.SportsEsports
        "work" -> Icons.Default.Work
        "trending_up" -> Icons.Default.TrendingUp
        "fitness_center" -> Icons.Default.FitnessCenter
        "receipt" -> Icons.Default.Receipt
        else -> Icons.Default.Receipt
    }
}

private fun formatCurrency(amount: Double): String {
    if (amount == 0.0) return "00"
    return java.text.NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(amount)
}
