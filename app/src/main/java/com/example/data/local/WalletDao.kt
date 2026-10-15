package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Account
import com.example.data.model.Budget
import com.example.data.model.Category
import com.example.data.model.PlannedPayment
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    // Accounts
    @Query("SELECT * FROM accounts ORDER BY id ASC")
    fun getAllAccounts(): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: Long): Account?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<Account>)

    @Update
    suspend fun updateAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    // Categories
    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Delete
    suspend fun deleteCategory(category: Category)

    // Budgets
    @Query("SELECT * FROM budgets ORDER BY id ASC")
    fun getAllBudgets(): Flow<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<Budget>)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    // Planned Payments
    @Query("SELECT * FROM planned_payments ORDER BY dueDate ASC")
    fun getAllPlannedPayments(): Flow<List<PlannedPayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlannedPayment(payment: PlannedPayment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlannedPayments(payments: List<PlannedPayment>)

    @Update
    suspend fun updatePlannedPayment(payment: PlannedPayment)

    @Delete
    suspend fun deletePlannedPayment(payment: PlannedPayment)

    // Clear all tables for demo reset
    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM accounts")
    suspend fun clearAccounts()

    @Query("DELETE FROM categories")
    suspend fun clearCategories()

    @Query("DELETE FROM budgets")
    suspend fun clearBudgets()

    @Query("DELETE FROM planned_payments")
    suspend fun clearPlannedPayments()
}
