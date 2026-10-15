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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DonutSmall
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Budget
import com.example.data.model.Category
import com.example.data.model.PlannedPayment
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PlanningSubTab
import com.example.ui.viewmodel.WalletViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlanningScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSubTab by viewModel.planningSubTab.collectAsStateWithLifecycle()
    val plannedPayments by viewModel.plannedPayments.collectAsStateWithLifecycle()
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("screen_planning"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Planning",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Manage budgets and scheduled bill payments",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // 2 Primary Planning Cards: Planned Payments (orange icon) and Budgets (blue icon)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Planned Payments (orange icon)
                PlanningCategoryCard(
                    title = "Planned Payments",
                    count = "${plannedPayments.count { !it.isPaid }} upcoming",
                    icon = Icons.Default.EventNote,
                    iconTint = AccentOrange,
                    isSelected = selectedSubTab == PlanningSubTab.PAYMENTS,
                    testTag = "card_planned_payments",
                    onClick = { viewModel.setPlanningSubTab(PlanningSubTab.PAYMENTS) },
                    modifier = Modifier.weight(1f)
                )

                // Card 2: Budgets (blue icon)
                PlanningCategoryCard(
                    title = "Budgets",
                    count = "${budgets.size} active",
                    icon = Icons.Default.DonutSmall,
                    iconTint = AccentBlue,
                    isSelected = selectedSubTab == PlanningSubTab.BUDGETS,
                    testTag = "card_budgets",
                    onClick = { viewModel.setPlanningSubTab(PlanningSubTab.BUDGETS) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Sub-view: Planned Payments
        if (selectedSubTab == PlanningSubTab.PAYMENTS) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming Bills & Payments",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 17.sp
                    )
                    Button(
                        onClick = { viewModel.showAddPlannedPaymentDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_add_planned_payment")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Bill",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (plannedPayments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No planned payments. Tap 'Add Bill' to schedule one!",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(plannedPayments) { payment ->
                    PlannedPaymentItem(
                        payment = payment,
                        currency = currentCurrency,
                        onTogglePaid = { viewModel.togglePlannedPaymentPaid(payment) },
                        onDelete = { viewModel.deletePlannedPayment(payment) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                    )
                }
            }
        } else {
            // Sub-view: Budgets
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Category Budgets",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 17.sp
                    )
                    Button(
                        onClick = { viewModel.showAddBudgetDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_create_budget")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New Budget",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (budgets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No budgets set. Tap 'New Budget' to set monthly limits!",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(budgets) { budget ->
                    val category = categories.find { it.id == budget.categoryId }
                    val spent = transactions
                        .filter { it.categoryId == budget.categoryId && it.type == "EXPENSE" }
                        .sumOf { it.amount }

                    BudgetItemCard(
                        budget = budget,
                        category = category,
                        spentAmount = spent,
                        currency = currentCurrency,
                        onDelete = { viewModel.deleteBudget(budget) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanningCategoryCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) iconTint else DarkBorder
    val bgColor = if (isSelected) iconTint.copy(alpha = 0.12f) else DarkSurface

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = count,
                fontSize = 12.sp,
                color = if (isSelected) iconTint else TextSecondary
            )
        }
    }
}

@Composable
private fun PlannedPaymentItem(
    payment: PlannedPayment,
    currency: String,
    onTogglePaid: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dueDateStr = dateFormat.format(Date(payment.dueDate))
    val isPastDue = payment.dueDate < System.currentTimeMillis() && !payment.isPaid

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(0.5.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            IconButton(onClick = onTogglePaid, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = if (payment.isPaid) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                    contentDescription = "Toggle Paid",
                    tint = if (payment.isPaid) AccentGreen else AccentOrange,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = payment.title,
                    fontWeight = FontWeight.SemiBold,
                    color = if (payment.isPaid) TextSecondary else TextPrimary,
                    fontSize = 15.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = if (isPastDue) AccentRed else TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (payment.isPaid) "Paid" else "Due $dueDateStr",
                        color = if (payment.isPaid) AccentGreen else if (isPastDue) AccentRed else TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${formatCurrency(payment.amount)} $currency",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (payment.isPaid) TextSecondary else AccentOrange
            )

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun BudgetItemCard(
    budget: Budget,
    category: Category?,
    spentAmount: Double,
    currency: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalBudget = budget.amount.coerceAtLeast(1.0)
    val ratio = (spentAmount / totalBudget).toFloat().coerceIn(0f, 1f)
    val remaining = budget.amount - spentAmount
    val isOverBudget = spentAmount > budget.amount

    val progressColor = when {
        isOverBudget -> AccentRed
        ratio > 0.85f -> AccentYellow
        else -> AccentGreen
    }

    val animatedProgress by animateFloatAsState(
        targetValue = ratio,
        animationSpec = tween(durationMillis = 600),
        label = "budget_progress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(0.5.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val catColor = category?.colorHex?.let { Color(it) } ?: AccentBlue
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(catColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category?.icon ?: "receipt"),
                        contentDescription = null,
                        tint = catColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = category?.name ?: "Category Budget",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 15.sp
                    )
                    Text(
                        text = budget.period,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DarkSurfaceElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(progressColor)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Spent vs Budget limit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Spent: ${formatCurrency(spentAmount)} $currency (${(ratio * 100).toInt()}%)",
                fontSize = 12.sp,
                color = progressColor,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (isOverBudget) "Over by ${formatCurrency(-remaining)} $currency" else "Left: ${formatCurrency(remaining)} $currency",
                fontSize = 12.sp,
                color = if (isOverBudget) AccentRed else TextSecondary
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }.format(amount)
}
