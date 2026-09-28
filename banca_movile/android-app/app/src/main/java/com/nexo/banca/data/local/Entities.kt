package com.nexo.banca.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: Int = 1,
    val holder: String,
    val number: String,
    val balance: Long,
    val savings: Long
)

@Entity(tableName = "movements")
data class MovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val timestamp: Long,
    val amount: Long,
    val accent: String
)

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey val id: String,
    val name: String,
    val amount: Long,
    val dueLabel: String,
    val accent: String,
    val paid: Boolean
)

@Entity(tableName = "card_settings")
data class CardSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val locked: Boolean,
    val international: Boolean,
    val contactless: Boolean
)
