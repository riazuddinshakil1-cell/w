package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.WalletViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddAccountDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    var accountName by remember { mutableStateOf("") }
    var balanceStr by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("bank") }
    var selectedColor by remember { mutableStateOf(0xFF0A84FF) }

    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()

    val icons = listOf("cash", "bank", "savings", "card")
    val colors = listOf(0xFF0A84FF, 0xFF30D158, 0xFFBF5AF2, 0xFFFF9F0A, 0xFFFF375F, 0xFF64D2FF)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        modifier = Modifier.testTag("dialog_add_account"),
        title = {
            Text(text = "Add Account", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("Account Name", color = TextSecondary) },
                    placeholder = { Text("e.g. City Bank", color = TextSecondary.copy(alpha = 0.5f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_account_name")
                )

                OutlinedTextField(
                    value = balanceStr,
                    onValueChange = { balanceStr = it },
                    label = { Text("Initial Balance ($currentCurrency)", color = TextSecondary) },
                    placeholder = { Text("0.00", color = TextSecondary.copy(alpha = 0.5f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_account_balance")
                )

                Text("Select Icon", color = TextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    icons.forEach { iconName ->
                        val isSelected = selectedIcon == iconName
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) AccentBlue.copy(alpha = 0.3f) else DarkSurfaceElevated)
                                .border(if (isSelected) 1.5.dp else 0.dp, AccentBlue, CircleShape)
                                .clickable { selectedIcon = iconName },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getAccountIcon(iconName),
                                contentDescription = null,
                                tint = if (isSelected) AccentBlue else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Text("Select Color", color = TextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    colors.forEach { colorHex ->
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bal = balanceStr.toDoubleOrNull() ?: 0.0
                    if (accountName.isNotBlank()) {
                        viewModel.createAccount(
                            name = accountName.trim(),
                            initialBalance = bal,
                            currency = currentCurrency,
                            icon = selectedIcon,
                            colorHex = selectedColor
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                modifier = Modifier.testTag("btn_confirm_add_account")
            ) {
                Text("Add Account", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddBudgetDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()
    var selectedCatId by remember { mutableStateOf(categories.firstOrNull()?.id ?: 1L) }
    var amountStr by remember { mutableStateOf("") }
    var period by remember { mutableStateOf("Monthly") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        modifier = Modifier.testTag("dialog_add_budget"),
        title = {
            Text(text = "New Budget Limit", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Category", color = TextSecondary, fontSize = 12.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories.filter { it.type != "INCOME" }) { cat ->
                        val isSelected = cat.id == selectedCatId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(cat.colorHex).copy(alpha = 0.25f) else DarkSurfaceElevated)
                                .border(if (isSelected) 1.5.dp else 0.dp, Color(cat.colorHex), RoundedCornerShape(12.dp))
                                .clickable { selectedCatId = cat.id }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat.name,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Budget Limit ($currentCurrency)", color = TextSecondary) },
                    placeholder = { Text("e.g. 5000", color = TextSecondary.copy(alpha = 0.5f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_budget_amount")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Monthly", "Weekly").forEach { p ->
                        val isSelected = period == p
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AccentBlue else DarkSurfaceElevated)
                                .clickable { period = p }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        viewModel.createBudget(selectedCatId, amt, period)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                modifier = Modifier.testTag("btn_confirm_add_budget")
            ) {
                Text("Set Budget", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddPlannedPaymentDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var daysUntilDue by remember { mutableStateOf(5) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 1L) }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: 1L) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        modifier = Modifier.testTag("dialog_add_planned_payment"),
        title = {
            Text(text = "Schedule Planned Bill", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Bill / Payment Title", color = TextSecondary) },
                    placeholder = { Text("e.g. Electric Bill", color = TextSecondary.copy(alpha = 0.5f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentOrange,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_planned_title")
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount ($currentCurrency)", color = TextSecondary) },
                    placeholder = { Text("0.00", color = TextSecondary.copy(alpha = 0.5f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentOrange,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_planned_amount")
                )

                Text("Due in", color = TextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(3 to "3 days", 7 to "1 week", 15 to "15 days", 30 to "1 month").forEach { (d, label) ->
                        val isSelected = daysUntilDue == d
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentOrange else DarkSurfaceElevated)
                                .clickable { daysUntilDue = d }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0) {
                        val dueMillis = System.currentTimeMillis() + (daysUntilDue * 24 * 60 * 60 * 1000L)
                        viewModel.createPlannedPayment(
                            title = title.trim(),
                            amount = amt,
                            dueDate = dueMillis,
                            accountId = selectedAccountId,
                            categoryId = selectedCategoryId
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                modifier = Modifier.testTag("btn_confirm_add_planned_payment")
            ) {
                Text("Schedule Bill", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// 1. Records Dialog: Full searchable & filterable transaction ledger
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("ALL") }

    val filtered = transactions.filter { tx ->
        val matchesType = when (filterType) {
            "EXPENSE" -> tx.type == "EXPENSE"
            "INCOME" -> tx.type == "INCOME"
            "TRANSFER" -> tx.type == "TRANSFER"
            else -> true
        }
        val matchesSearch = if (searchQuery.isBlank()) true else {
            val catName = categories.find { it.id == tx.categoryId }?.name ?: ""
            tx.notes.contains(searchQuery, ignoreCase = true) ||
            tx.labels.contains(searchQuery, ignoreCase = true) ||
            catName.contains(searchQuery, ignoreCase = true)
        }
        matchesType && matchesSearch
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        dragHandle = null,
        modifier = Modifier.testTag("dialog_records")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                }
                Text(
                    text = "Transaction Records (${filtered.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search records by note or category...", color = TextSecondary.copy(alpha = 0.6f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            )

            // Filter Tabs: All, Expense, Income, Transfer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "All", "EXPENSE" to "Expenses", "INCOME" to "Income", "TRANSFER" to "Transfers").forEach { (type, label) ->
                    val isSelected = filterType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AccentBlue else DarkSurfaceElevated)
                            .clickable { filterType = type }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No transactions found.", color = TextSecondary)
                        }
                    }
                } else {
                    items(filtered) { tx ->
                        val category = categories.find { it.id == tx.categoryId }
                        val account = accounts.find { it.id == tx.accountId }
                        TransactionItemRow(
                            transaction = tx,
                            category = category,
                            account = account,
                            currency = currentCurrency,
                            onDelete = { viewModel.deleteTransaction(tx) }
                        )
                    }
                }
            }
        }
    }
}

// 2. Savings Goals Dialog: Free Milestone & Target Fund Planner
@Composable
fun SavingsGoalsDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    var emergencySaved by remember { mutableDoubleStateOf(35000.0) }
    val emergencyTarget = 60000.0
    var vacationSaved by remember { mutableDoubleStateOf(12000.0) }
    val vacationTarget = 25000.0
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        modifier = Modifier.testTag("dialog_savings_goals"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AccentPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Savings Goals", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                GoalProgressItem(
                    title = "Emergency Safety Fund",
                    current = emergencySaved,
                    target = emergencyTarget,
                    currency = currentCurrency,
                    color = AccentGreen,
                    onAdd = { emergencySaved += 2500.0 }
                )

                GoalProgressItem(
                    title = "Holiday & Travel Fund",
                    current = vacationSaved,
                    target = vacationTarget,
                    currency = currentCurrency,
                    color = AccentPurple,
                    onAdd = { vacationSaved += 1500.0 }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
            ) {
                Text("Done", color = Color.White)
            }
        }
    )
}

@Composable
private fun GoalProgressItem(
    title: String,
    current: Double,
    target: Double,
    currency: String,
    color: Color,
    onAdd: () -> Unit
) {
    val ratio = (current / target).toFloat().coerceIn(0f, 1f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
            TextButton(onClick = onAdd) {
                Text("+ Contribute", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF38383A))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${formatDialogAmount(current)} / ${formatDialogAmount(target)} $currency",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Text(
                text = "${(ratio * 100).toInt()}%",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

// 3. ShareCost Dialog: Interactive Bill Splitter Calculator
@Composable
fun ShareCostDialog(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()
    var billAmountStr by remember { mutableStateOf("2400") }
    var peopleCount by remember { mutableIntStateOf(4) }
    var tipPercent by remember { mutableIntStateOf(10) }

    val billTotal = billAmountStr.toDoubleOrNull() ?: 0.0
    val tipAmount = billTotal * (tipPercent / 100.0)
    val grandTotal = billTotal + tipAmount
    val perPerson = if (peopleCount > 0) grandTotal / peopleCount else 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        modifier = Modifier.testTag("dialog_share_cost"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CallSplit, contentDescription = null, tint = AccentPink)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ShareCost Splitter", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = billAmountStr,
                    onValueChange = { billAmountStr = it },
                    label = { Text("Total Bill ($currentCurrency)", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentPink,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // People stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Number of People", color = TextSecondary, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (peopleCount > 1) peopleCount-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = AccentPink)
                        }
                        Text(
                            text = "$peopleCount",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { peopleCount++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = AccentPink)
                        }
                    }
                }

                // Tip Chips
                Text(text = "Tip / Service Fee", color = TextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0, 5, 10, 15).forEach { pct ->
                        val isSelected = tipPercent == pct
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentPink else DarkSurfaceElevated)
                                .clickable { tipPercent = pct }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$pct%",
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Results Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentPink.copy(alpha = 0.15f))
                        .border(1.dp, AccentPink.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "EACH PERSON PAYS", color = AccentPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${formatDialogAmount(perPerson)} $currentCurrency",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Grand Total: ${formatDialogAmount(grandTotal)} $currentCurrency (Tip: ${formatDialogAmount(tipAmount)})",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
            ) {
                Text("Done", color = Color.White)
            }
        }
    )
}

@Composable
fun NotificationsDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        modifier = Modifier.testTag("dialog_notifications"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Notifications", color = TextPrimary, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NotificationRow(
                    title = "Budget Alert: Food & Dining",
                    body = "You have used 85% of your monthly food budget limit.",
                    time = "2 hours ago",
                    color = AccentOrange
                )
                NotificationRow(
                    title = "Planned Payment Reminder",
                    body = "Fiber Internet bill (1,200 ৳) is due in 3 days.",
                    time = "Yesterday",
                    color = AccentBlue
                )
                NotificationRow(
                    title = "Weekly Balance Growth",
                    body = "Your net worth increased by +4.8% this week. Great job!",
                    time = "3 days ago",
                    color = AccentGreen
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Mark All as Read", color = Color.White)
            }
        }
    )
}

@Composable
private fun NotificationRow(
    title: String,
    body: String,
    time: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
                .padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 13.sp)
            Text(text = body, color = TextSecondary, fontSize = 11.sp)
            Text(text = time, color = TextSecondary.copy(alpha = 0.6f), fontSize = 10.sp)
        }
    }
}

private fun formatDialogAmount(amount: Double): String {
    return NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(amount)
}
