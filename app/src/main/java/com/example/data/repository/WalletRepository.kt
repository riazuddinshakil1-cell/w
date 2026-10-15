package com.example.data.repository

import com.example.data.local.WalletDao
import com.example.data.model.Account
import com.example.data.model.Budget
import com.example.data.model.Category
import com.example.data.model.PlannedPayment
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WalletRepository(private val dao: WalletDao) {
    val accounts: Flow<List<Account>> = dao.getAllAccounts()
    val transactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val categories: Flow<List<Category>> = dao.getAllCategories()
    val budgets: Flow<List<Budget>> = dao.getAllBudgets()
    val plannedPayments: Flow<List<PlannedPayment>> = dao.getAllPlannedPayments()

    suspend fun addAccount(account: Account): Long = dao.insertAccount(account)
    suspend fun updateAccount(account: Account) = dao.updateAccount(account)
    suspend fun deleteAccount(account: Account) = dao.deleteAccount(account)

    suspend fun addTransaction(transaction: Transaction): Long {
        val id = dao.insertTransaction(transaction)
        // Adjust account balances accordingly
        val sourceAccount = dao.getAccountById(transaction.accountId)
        if (sourceAccount != null) {
            when (transaction.type) {
                "EXPENSE" -> {
                    dao.updateAccount(sourceAccount.copy(balance = sourceAccount.balance - transaction.amount))
                }
                "INCOME" -> {
                    dao.updateAccount(sourceAccount.copy(balance = sourceAccount.balance + transaction.amount))
                }
                "TRANSFER" -> {
                    dao.updateAccount(sourceAccount.copy(balance = sourceAccount.balance - transaction.amount))
                    if (transaction.toAccountId != null) {
                        val targetAccount = dao.getAccountById(transaction.toAccountId)
                        if (targetAccount != null) {
                            dao.updateAccount(targetAccount.copy(balance = targetAccount.balance + transaction.amount))
                        }
                    }
                }
            }
        }
        return id
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction)
        // Revert balance
        val account = dao.getAccountById(transaction.accountId)
        if (account != null) {
            when (transaction.type) {
                "EXPENSE" -> dao.updateAccount(account.copy(balance = account.balance + transaction.amount))
                "INCOME" -> dao.updateAccount(account.copy(balance = account.balance - transaction.amount))
                "TRANSFER" -> {
                    dao.updateAccount(account.copy(balance = account.balance + transaction.amount))
                    if (transaction.toAccountId != null) {
                        val target = dao.getAccountById(transaction.toAccountId)
                        if (target != null) {
                            dao.updateAccount(target.copy(balance = target.balance - transaction.amount))
                        }
                    }
                }
            }
        }
    }

    suspend fun addCategory(category: Category): Long = dao.insertCategory(category)
    suspend fun deleteCategory(category: Category) = dao.deleteCategory(category)

    suspend fun addBudget(budget: Budget): Long = dao.insertBudget(budget)
    suspend fun deleteBudget(budget: Budget) = dao.deleteBudget(budget)

    suspend fun addPlannedPayment(payment: PlannedPayment): Long = dao.insertPlannedPayment(payment)
    suspend fun updatePlannedPayment(payment: PlannedPayment) = dao.updatePlannedPayment(payment)
    suspend fun deletePlannedPayment(payment: PlannedPayment) = dao.deletePlannedPayment(payment)

    suspend fun updateCurrencyForAccounts(newCurrency: String) {
        val currentAccounts = dao.getAllAccounts().first()
        currentAccounts.forEach { acc ->
            dao.updateAccount(acc.copy(currency = newCurrency))
        }
    }

    suspend fun seedDefaultDataIfEmpty() {
        val existingAccounts = dao.getAllAccounts().first()
        if (existingAccounts.isNotEmpty()) {
            existingAccounts.forEach { acc ->
                if (acc.balance == 1150.0) {
                    dao.updateAccount(acc.copy(balance = 0.0))
                }
            }
            return
        }

        // 1. Initial Accounts with 0.0 starter balance (00৳)
        val defaultAccounts = listOf(
            Account(id = 1, name = "CASH", balance = 0.0, currency = "৳", icon = "cash", colorHex = 0xFF30D158),
            Account(id = 2, name = "BANK", balance = 0.0, currency = "৳", icon = "bank", colorHex = 0xFF0A84FF),
            Account(id = 3, name = "SAVINGS", balance = 0.0, currency = "৳", icon = "savings", colorHex = 0xFFBF5AF2)
        )
        dao.insertAccounts(defaultAccounts)

        // 2. Default Categories
        val defaultCategories = listOf(
            Category(id = 1, name = "Food & Dining", icon = "restaurant", colorHex = 0xFFFF9F0A, type = "EXPENSE"),
            Category(id = 2, name = "Shopping", icon = "shopping_bag", colorHex = 0xFFFF375F, type = "EXPENSE"),
            Category(id = 3, name = "Housing & Rent", icon = "home", colorHex = 0xFF0A84FF, type = "EXPENSE"),
            Category(id = 4, name = "Transportation", icon = "directions_car", colorHex = 0xFF64D2FF, type = "EXPENSE"),
            Category(id = 5, name = "Entertainment", icon = "sports_esports", colorHex = 0xFFBF5AF2, type = "EXPENSE"),
            Category(id = 6, name = "Salary", icon = "work", colorHex = 0xFF30D158, type = "INCOME"),
            Category(id = 7, name = "Investments", icon = "trending_up", colorHex = 0xFF40C8E0, type = "INCOME"),
            Category(id = 8, name = "Health & Fitness", icon = "fitness_center", colorHex = 0xFFFF453A, type = "EXPENSE"),
            Category(id = 9, name = "Bills & Utilities", icon = "receipt", colorHex = 0xFFFFD60A, type = "EXPENSE")
        )
        dao.insertCategories(defaultCategories)

        // 3. Budgets
        val defaultBudgets = listOf(
            Budget(id = 1, categoryId = 1, amount = 15000.0, period = "Monthly"), // Food
            Budget(id = 2, categoryId = 2, amount = 8000.0, period = "Monthly"),  // Shopping
            Budget(id = 3, categoryId = 4, amount = 4500.0, period = "Monthly"),  // Transport
            Budget(id = 4, categoryId = 5, amount = 3000.0, period = "Monthly")   // Entertainment
        )
        dao.insertBudgets(defaultBudgets)

        // 4. Planned Payments
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L
        val defaultPlanned = listOf(
            PlannedPayment(
                id = 1,
                title = "High-speed Fiber Internet",
                amount = 1200.0,
                dueDate = now + (3 * oneDay),
                accountId = 2,
                categoryId = 9,
                isPaid = false,
                frequency = "Monthly"
            ),
            PlannedPayment(
                id = 2,
                title = "Netflix Premium Plan",
                amount = 450.0,
                dueDate = now + (8 * oneDay),
                accountId = 2,
                categoryId = 5,
                isPaid = false,
                frequency = "Monthly"
            ),
            PlannedPayment(
                id = 3,
                title = "Apartment Maintenance",
                amount = 3500.0,
                dueDate = now + (15 * oneDay),
                accountId = 1,
                categoryId = 3,
                isPaid = false,
                frequency = "Monthly"
            )
        )
        dao.insertPlannedPayments(defaultPlanned)

        // 5. Transaction ledger starts clean (empty) for authentic tracking
    }

    suspend fun resetAllDemoData() {
        dao.clearTransactions()
        dao.clearAccounts()
        dao.clearCategories()
        dao.clearBudgets()
        dao.clearPlannedPayments()
        seedDefaultDataIfEmpty()
    }
}
