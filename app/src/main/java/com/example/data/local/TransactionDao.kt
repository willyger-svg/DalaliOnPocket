package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM boosted_listings")
    fun getAllBoostedListings(): Flow<List<BoostedListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoost(boost: BoostedListingEntity)

    @Query("SELECT * FROM title_verifications ORDER BY submittedAt DESC")
    fun getAllTitleVerifications(): Flow<List<TitleVerificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTitleVerification(verification: TitleVerificationEntity)
}
