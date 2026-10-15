package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String, // EXPENSE, INCOME, TRANSFER
    val categoryId: Long,
    val accountId: Long,
    val toAccountId: Long? = null,
    val dateTime: Long = System.currentTimeMillis(),
    val labels: String = "",
    val notes: String = ""
)
