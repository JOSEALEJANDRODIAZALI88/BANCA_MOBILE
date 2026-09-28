package com.nexo.banca.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NexoDao {
    @Query("SELECT * FROM accounts WHERE id = 1")
    fun observeAccount(): Flow<AccountEntity?>

    @Query("SELECT * FROM accounts WHERE id = 1")
    suspend fun account(): AccountEntity?

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun accountCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Query("SELECT * FROM movements ORDER BY timestamp DESC, id DESC")
    fun observeMovements(): Flow<List<MovementEntity>>

    @Insert
    suspend fun insertMovement(movement: MovementEntity)

    @Insert
    suspend fun insertMovements(movements: List<MovementEntity>)

    @Query("SELECT * FROM bills ORDER BY id")
    fun observeBills(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE id = :id")
    suspend fun bill(id: String): BillEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBills(bills: List<BillEntity>)

    @Update
    suspend fun updateBill(bill: BillEntity)

    @Query("SELECT * FROM card_settings WHERE id = 1")
    fun observeCardSettings(): Flow<CardSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCardSettings(settings: CardSettingsEntity)

    @Query("UPDATE card_settings SET locked = :value WHERE id = 1")
    suspend fun updateCardLocked(value: Boolean)

    @Query("UPDATE card_settings SET international = :value WHERE id = 1")
    suspend fun updateCardInternational(value: Boolean)

    @Query("UPDATE card_settings SET contactless = :value WHERE id = 1")
    suspend fun updateCardContactless(value: Boolean)
}
