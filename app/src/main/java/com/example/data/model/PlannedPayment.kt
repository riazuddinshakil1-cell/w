package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planned_payments")
data class PlannedPayment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val dueDate: Long,
    val accountId: Long = 1,
    val categoryId: Long = 1,
    val isPaid: Boolean = false,
    val frequency: String = "Monthly"
)
