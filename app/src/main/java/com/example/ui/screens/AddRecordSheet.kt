package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Label
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Account
import com.example.data.model.Category
import com.example.ui.components.NumericKeypad
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddRecordSheet(
    viewModel: WalletViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val recordType by viewModel.recordType.collectAsStateWithLifecycle()
    val amountInput by viewModel.amountInput.collectAsStateWithLifecycle()
    val selectedAccountId by viewModel.selectedAccountId.collectAsStateWithLifecycle()
    val selectedToAccountId by viewModel.selectedToAccountId.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val notes by viewModel.recordNotes.collectAsStateWithLifecycle()
    val labels by viewModel.recordLabels.collectAsStateWithLifecycle()

    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()

    val isExpense = recordType == "EXPENSE"
    val isIncome = recordType == "INCOME"
    val isTransfer = recordType == "TRANSFER"

    val primaryColor = when {
        isExpense -> AccentRed
        isIncome -> AccentGreen
        else -> AccentBlue
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        dragHandle = null,
        modifier = Modifier.testTag("add_record_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // Top Bar with Close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_add_record")) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Add Record",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Spacer to balance
                Spacer(modifier = Modifier.size(48.dp))
            }

            // 1. Top Toggle: Expense (selected by default), Income, Transfer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceElevated)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RecordTypeToggleItem(
                    title = "Expense",
                    isSelected = isExpense,
                    activeColor = AccentRed,
                    onClick = { viewModel.setRecordType("EXPENSE") },
                    modifier = Modifier.weight(1f)
                )
                RecordTypeToggleItem(
                    title = "Income",
                    isSelected = isIncome,
                    activeColor = AccentGreen,
                    onClick = { viewModel.setRecordType("INCOME") },
                    modifier = Modifier.weight(1f)
                )
                RecordTypeToggleItem(
                    title = "Transfer",
                    isSelected = isTransfer,
                    activeColor = AccentBlue,
                    onClick = { viewModel.setRecordType("TRANSFER") },
                    modifier = Modifier.weight(1f)
                )
            }

            // 2. Big Amount Display
            val prefix = when {
                isExpense -> "-"
                isIncome -> "+"
                else -> ""
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                val displayAmount = if (amountInput == "0") "00" else amountInput
                Text(
                    text = "$prefix$displayAmount$currentCurrency",
                    color = primaryColor,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("txt_big_amount")
                )
            }

            // 3. Form Field: Account Selector
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Text(
                    text = if (isTransfer) "From Account" else "Account",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(accounts) { account ->
                        val isSelected = account.id == selectedAccountId
                        AccountChip(
                            account = account,
                            isSelected = isSelected,
                            onClick = { viewModel.setSelectedAccount(account.id) }
                        )
                    }
                }
            }

            // Target account if Transfer
            if (isTransfer) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    Text(
                        text = "To Account",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(accounts.filter { it.id != selectedAccountId }) { account ->
                            val isSelected = account.id == selectedToAccountId
                            AccountChip(
                                account = account,
                                isSelected = isSelected,
                                onClick = { viewModel.setSelectedToAccount(account.id) }
                            )
                        }
                    }
                }
            }

            // 4. Form Field: Category Selector (required)
            if (!isTransfer) {
                val filteredCategories = categories.filter {
                    if (isExpense) it.type != "INCOME" else it.type != "EXPENSE"
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text(
                        text = "Category (required)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredCategories.forEach { category ->
                            val isSelected = category.id == selectedCategoryId
                            CategoryChip(
                                category = category,
                                isSelected = isSelected,
                                onClick = { viewModel.setSelectedCategory(category.id) }
                            )
                        }
                    }
                }
            }

            // 5. Notes / Labels Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { viewModel.setRecordNotes(it) },
                    label = { Text("Note", color = TextSecondary) },
                    placeholder = { Text("e.g. Lunch with team", color = TextSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.weight(1f).testTag("input_record_notes"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = labels,
                    onValueChange = { viewModel.setRecordLabels(it) },
                    label = { Text("Label", color = TextSecondary) },
                    placeholder = { Text("e.g. Work", color = TextSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.weight(1f).testTag("input_record_labels"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // 6. Bottom Custom Numeric Keypad
            NumericKeypad(
                onDigit = { viewModel.onKeypadDigit(it) },
                onBackspace = { viewModel.onKeypadBackspace() },
                modifier = Modifier.fillMaxWidth()
            )

            // 7. Save Button
            val canSave = (amountInput.toDoubleOrNull() ?: 0.0) > 0.0
            Button(
                onClick = { viewModel.saveCurrentRecord() },
                enabled = canSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    disabledContainerColor = DarkSurfaceElevated
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .height(50.dp)
                    .testTag("btn_save_record")
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = if (canSave) Color.White else TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        isExpense -> "Add Expense"
                        isIncome -> "Add Income"
                        else -> "Confirm Transfer"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (canSave) Color.White else TextSecondary
                )
            }
        }
    }
}

@Composable
private fun RecordTypeToggleItem(
    title: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) activeColor else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun AccountChip(
    account: Account,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) AccentBlue else DarkBorder
    val bgColor = if (isSelected) AccentBlue.copy(alpha = 0.2f) else DarkSurface

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = getAccountIcon(account.icon),
            contentDescription = null,
            tint = if (isSelected) AccentBlue else TextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = account.name,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) TextPrimary else TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val catColor = Color(category.colorHex)
    val borderColor = if (isSelected) catColor else DarkBorder
    val bgColor = if (isSelected) catColor.copy(alpha = 0.25f) else DarkSurface

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = getCategoryIcon(category.icon),
            contentDescription = null,
            tint = catColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = category.name,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) TextPrimary else TextSecondary,
            fontSize = 12.sp
        )
    }
}
