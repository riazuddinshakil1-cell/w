package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.WalletDatabase
import com.example.data.model.Account
import com.example.data.model.Budget
import com.example.data.model.Category
import com.example.data.model.PlannedPayment
import com.example.data.model.Transaction
import com.example.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppTab {
    DASHBOARD,
    PLANNING,
    STATISTICS,
    MORE
}

enum class PlanningSubTab {
    PAYMENTS,
    BUDGETS
}

enum class StatsPeriod(val title: String) {
    THIS_MONTH("This month"),
    LAST_MONTH("Last month"),
    THIS_YEAR("This year")
}

data class CategorySpend(
    val category: Category,
    val totalSpent: Double,
    val percentage: Float
)

data class TrendPoint(
    val label: String,
    val balance: Double
)

class WalletViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WalletRepository

    init {
        val db = WalletDatabase.getDatabase(application)
        repository = WalletRepository(db.walletDao())
        viewModelScope.launch {
            repository.seedDefaultDataIfEmpty()
        }
    }

    // Navigation and Tab State
    private val _currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // Modal dialog controls - All 100% Free & Functional
    val showAddRecordSheet = MutableStateFlow(false)
    val showAddAccountDialog = MutableStateFlow(false)
    val showNotificationsDialog = MutableStateFlow(false)
    val showSettingsDialog = MutableStateFlow(false)
    val showAddBudgetDialog = MutableStateFlow(false)
    val showAddPlannedPaymentDialog = MutableStateFlow(false)
    val showRecordsDialog = MutableStateFlow(false)
    val showSavingsGoalsDialog = MutableStateFlow(false)
    val showShareCostDialog = MutableStateFlow(false)

    // Planning sub-tab
    private val _planningSubTab = MutableStateFlow(PlanningSubTab.PAYMENTS)
    val planningSubTab: StateFlow<PlanningSubTab> = _planningSubTab.asStateFlow()
    fun setPlanningSubTab(tab: PlanningSubTab) {
        _planningSubTab.value = tab
    }

    // Statistics period selector
    private val _statsPeriod = MutableStateFlow(StatsPeriod.THIS_MONTH)
    val statsPeriod: StateFlow<StatsPeriod> = _statsPeriod.asStateFlow()
    fun setStatsPeriod(period: StatsPeriod) {
        _statsPeriod.value = period
    }
    fun nextStatsPeriod() {
        val values = StatsPeriod.values()
        val nextIdx = (_statsPeriod.value.ordinal + 1) % values.size
        _statsPeriod.value = values[nextIdx]
    }
    fun prevStatsPeriod() {
        val values = StatsPeriod.values()
        val prevIdx = if (_statsPeriod.value.ordinal - 1 < 0) values.size - 1 else _statsPeriod.value.ordinal - 1
        _statsPeriod.value = values[prevIdx]
    }

    // Raw Flows from repository
    val accounts: StateFlow<List<Account>> = repository.accounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<Transaction>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<Budget>> = repository.budgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val plannedPayments: StateFlow<List<PlannedPayment>> = repository.plannedPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated overall balance
    val totalBalance: StateFlow<Double> = accounts.combine(MutableStateFlow(Unit)) { accs, _ ->
        accs.sumOf { it.balance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val currentCurrency: StateFlow<String> = accounts.combine(MutableStateFlow(Unit)) { accs, _ ->
        accs.firstOrNull()?.currency ?: "৳"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "৳")

    // Filter transactions dynamically by selected period
    private fun isTransactionInPeriod(timestamp: Long, period: StatsPeriod): Boolean {
        val calNow = Calendar.getInstance()
        val calTx = Calendar.getInstance().apply { timeInMillis = timestamp }
        return when (period) {
            StatsPeriod.THIS_MONTH -> {
                calNow.get(Calendar.YEAR) == calTx.get(Calendar.YEAR) &&
                calNow.get(Calendar.MONTH) == calTx.get(Calendar.MONTH)
            }
            StatsPeriod.LAST_MONTH -> {
                val calLast = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
                calLast.get(Calendar.YEAR) == calTx.get(Calendar.YEAR) &&
                calLast.get(Calendar.MONTH) == calTx.get(Calendar.MONTH)
            }
            StatsPeriod.THIS_YEAR -> {
                calNow.get(Calendar.YEAR) == calTx.get(Calendar.YEAR)
            }
        }
    }

    // Filtered transactions for active period
    val periodTransactions: StateFlow<List<Transaction>> = combine(transactions, _statsPeriod) { txs, period ->
        txs.filter { isTransactionInPeriod(it.dateTime, period) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated Cash Flow & Category breakdown for the active period
    val categorySpends: StateFlow<List<CategorySpend>> = combine(periodTransactions, categories) { txs, cats ->
        val expenseTxs = txs.filter { it.type == "EXPENSE" }
        val totalExpense = expenseTxs.sumOf { it.amount }
        if (totalExpense <= 0) return@combine emptyList<CategorySpend>()

        cats.mapNotNull { cat ->
            val spent = expenseTxs.filter { it.categoryId == cat.id }.sumOf { it.amount }
            if (spent > 0) {
                CategorySpend(
                    category = cat,
                    totalSpent = spent,
                    percentage = (spent / totalExpense).toFloat()
                )
            } else null
        }.sortedByDescending { it.totalSpent }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncomeThisPeriod: StateFlow<Double> = periodTransactions.combine(MutableStateFlow(Unit)) { txs, _ ->
        txs.filter { it.type == "INCOME" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpenseThisPeriod: StateFlow<Double> = periodTransactions.combine(MutableStateFlow(Unit)) { txs, _ ->
        txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Dynamic Balance Trend Points computed directly from real ledger transactions
    val balanceTrendPoints: StateFlow<List<TrendPoint>> = combine(accounts, transactions) { accs, txs ->
        val currentTotal = accs.sumOf { it.balance }
        val dayMillis = 24 * 60 * 60 * 1000L
        val now = System.currentTimeMillis()
        val sdfDay = SimpleDateFormat("EEE", Locale.US)

        val points = mutableListOf<TrendPoint>()
        for (i in 6 downTo 0) {
            val targetDayEnd = now - (i * dayMillis)
            // Reverse delta for transactions that happened after targetDayEnd
            val netAfter = txs.filter { it.dateTime > targetDayEnd }.sumOf { tx ->
                when (tx.type) {
                    "INCOME" -> tx.amount
                    "EXPENSE" -> -tx.amount
                    else -> 0.0
                }
            }
            val balanceAtDay = (currentTotal - netAfter).coerceAtLeast(0.0)
            val dayLabel = sdfDay.format(Date(targetDayEnd))
            points.add(TrendPoint(dayLabel, balanceAtDay))
        }
        points
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Add Record Form State
    private val _recordType = MutableStateFlow("EXPENSE") // EXPENSE, INCOME, TRANSFER
    val recordType: StateFlow<String> = _recordType.asStateFlow()

    private val _amountInput = MutableStateFlow("0")
    val amountInput: StateFlow<String> = _amountInput.asStateFlow()

    private val _selectedAccountId = MutableStateFlow(1L)
    val selectedAccountId: StateFlow<Long> = _selectedAccountId.asStateFlow()

    private val _selectedToAccountId = MutableStateFlow<Long?>(null)
    val selectedToAccountId: StateFlow<Long?> = _selectedToAccountId.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow(1L)
    val selectedCategoryId: StateFlow<Long> = _selectedCategoryId.asStateFlow()

    private val _recordNotes = MutableStateFlow("")
    val recordNotes: StateFlow<String> = _recordNotes.asStateFlow()

    private val _recordLabels = MutableStateFlow("")
    val recordLabels: StateFlow<String> = _recordLabels.asStateFlow()

    fun setRecordType(type: String) {
        _recordType.value = type
    }

    fun setSelectedAccount(id: Long) {
        _selectedAccountId.value = id
    }

    fun setSelectedToAccount(id: Long?) {
        _selectedToAccountId.value = id
    }

    fun setSelectedCategory(id: Long) {
        _selectedCategoryId.value = id
    }

    fun setRecordNotes(notes: String) {
        _recordNotes.value = notes
    }

    fun setRecordLabels(labels: String) {
        _recordLabels.value = labels
    }

    // Keypad actions
    fun onKeypadDigit(char: String) {
        val current = _amountInput.value
        if (char == ".") {
            if (!current.contains(".")) {
                _amountInput.value = current + "."
            }
        } else {
            if (current == "0") {
                _amountInput.value = char
            } else {
                val dotIdx = current.indexOf('.')
                if (dotIdx == -1 || current.length - dotIdx <= 2) {
                    if (current.length < 9) {
                        _amountInput.value = current + char
                    }
                }
            }
        }
    }

    fun onKeypadBackspace() {
        val current = _amountInput.value
        if (current.length <= 1) {
            _amountInput.value = "0"
        } else {
            _amountInput.value = current.dropLast(1)
        }
    }

    fun onKeypadClear() {
        _amountInput.value = "0"
    }

    fun saveCurrentRecord(onSuccess: () -> Unit = {}) {
        val amount = _amountInput.value.toDoubleOrNull() ?: 0.0
        if (amount <= 0) return

        viewModelScope.launch {
            val tx = Transaction(
                amount = amount,
                type = _recordType.value,
                categoryId = _selectedCategoryId.value,
                accountId = _selectedAccountId.value,
                toAccountId = if (_recordType.value == "TRANSFER") _selectedToAccountId.value else null,
                dateTime = System.currentTimeMillis(),
                labels = _recordLabels.value,
                notes = _recordNotes.value
            )
            repository.addTransaction(tx)
            // Reset form
            _amountInput.value = "0"
            _recordNotes.value = ""
            _recordLabels.value = ""
            showAddRecordSheet.value = false
            onSuccess()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // Account creation
    fun createAccount(name: String, initialBalance: Double, currency: String, icon: String, colorHex: Long) {
        viewModelScope.launch {
            repository.addAccount(
                Account(
                    name = name,
                    balance = initialBalance,
                    currency = currency,
                    icon = icon,
                    colorHex = colorHex
                )
            )
            showAddAccountDialog.value = false
        }
    }

    // Budget creation
    fun createBudget(categoryId: Long, amount: Double, period: String = "Monthly") {
        viewModelScope.launch {
            repository.addBudget(
                Budget(
                    categoryId = categoryId,
                    amount = amount,
                    period = period
                )
            )
            showAddBudgetDialog.value = false
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    // Planned Payment actions
    fun createPlannedPayment(title: String, amount: Double, dueDate: Long, accountId: Long, categoryId: Long) {
        viewModelScope.launch {
            repository.addPlannedPayment(
                PlannedPayment(
                    title = title,
                    amount = amount,
                    dueDate = dueDate,
                    accountId = accountId,
                    categoryId = categoryId,
                    isPaid = false
                )
            )
            showAddPlannedPaymentDialog.value = false
        }
    }

    fun togglePlannedPaymentPaid(payment: PlannedPayment) {
        viewModelScope.launch {
            val updated = payment.copy(isPaid = !payment.isPaid)
            repository.updatePlannedPayment(updated)
            // If marked as paid, automatically log as an expense transaction!
            if (updated.isPaid) {
                repository.addTransaction(
                    Transaction(
                        amount = payment.amount,
                        type = "EXPENSE",
                        categoryId = payment.categoryId,
                        accountId = payment.accountId,
                        dateTime = System.currentTimeMillis(),
                        labels = "Planned Payment",
                        notes = payment.title
                    )
                )
            }
        }
    }

    fun deletePlannedPayment(payment: PlannedPayment) {
        viewModelScope.launch {
            repository.deletePlannedPayment(payment)
        }
    }

    // Currency Switcher
    fun changeCurrency(newCurrency: String) {
        viewModelScope.launch {
            repository.updateCurrencyForAccounts(newCurrency)
        }
    }

    // Reset Demo Data
    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetAllDemoData()
        }
    }

    // Helper formatter
    fun formatAmount(amount: Double, currency: String? = null): String {
        val curr = currency ?: currentCurrency.value
        if (amount == 0.0) return "00$curr"
        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return "${formatter.format(amount)} $curr"
    }
}
