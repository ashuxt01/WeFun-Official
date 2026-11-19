package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow
import androidx.room.Query

@Entity(tableName = "vip_tier_configs")
data class VipConfigEntity(
    @PrimaryKey val level: Int,
    val name: String,
    val badgeTitle: String,
    val growthThreshold: Long,
    val regulationDays: Int,
    val regulationTarget: Long,
    val growthToDeduct: Long,
    val dailyDiamondBonus: Long,
    val monthlyDiamondSummary: Long,
    val friendLimitIncrement: Int,
    val nicknameColorTierName: String,
    val nameplateTier: String,
    val chatBubbleTier: String,
    val voiceBubbleTier: String,
    val entranceEffectTitle: String,
    val monthlySubscriptionCoins: Long,
    val accentHex: Long,
    val secondaryAccentHex: Long,
    val privilegesPipe: String,
    val privilegeKeysPipe: String,
)

@Entity(tableName = "vip_growth_history")
data class GrowthPointHistoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val sourceCategoryName: String,
    val diamondsSpent: Long,
    val pointsDelta: Long,
    val regulationPointsDelta: Long,
    val balanceAfter: Long,
    val levelBefore: Int,
    val levelAfter: Int,
    val description: String,
    val referenceId: String,
    val timestamp: Long,
)

@Entity(tableName = "vip_payment_history")
data class VipPaymentHistoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val planTitle: String,
    val months: Int,
    val targetLevel: Int,
    val amountDisplay: String,
    val coinsSpent: Long,
    val status: String,
    val referenceId: String,
    val timestamp: Long,
)

@Entity(tableName = "vip_audit_logs")
data class VipAuditLogEntity(
    @PrimaryKey val id: String,
    val actorName: String,
    val actionType: String,
    val targetUserId: String,
    val summary: String,
    val timestamp: Long,
)

@Dao
interface VipEcosystemDao {
    @Query("SELECT * FROM vip_tier_configs ORDER BY level ASC")
    fun observeVipConfigs(): Flow<List<VipConfigEntity>>

    @Query("SELECT * FROM vip_tier_configs ORDER BY level ASC")
    suspend fun getVipConfigs(): List<VipConfigEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVipConfigs(items: List<VipConfigEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVipConfig(item: VipConfigEntity)

    @Query("SELECT COUNT(*) FROM vip_tier_configs")
    suspend fun countVipConfigs(): Int

    @Query("SELECT * FROM vip_growth_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun observeGrowthHistory(userId: String): Flow<List<GrowthPointHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertGrowthHistory(entry: GrowthPointHistoryEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM vip_growth_history WHERE referenceId = :referenceId)")
    suspend fun growthReferenceExists(referenceId: String): Boolean

    @Query("SELECT COUNT(*) FROM vip_growth_history")
    suspend fun countGrowthHistory(): Int

    @Query("SELECT * FROM vip_payment_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun observePaymentHistory(userId: String): Flow<List<VipPaymentHistoryEntity>>

    @Query("SELECT * FROM vip_payment_history WHERE userId = :userId AND status = 'PENDING_RESTORE' ORDER BY timestamp DESC")
    suspend fun getPendingRestorePayments(userId: String): List<VipPaymentHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPaymentHistory(entry: VipPaymentHistoryEntity)

    @Query("SELECT COUNT(*) FROM vip_payment_history")
    suspend fun countPaymentHistory(): Int

    @Query("SELECT * FROM vip_audit_logs ORDER BY timestamp DESC")
    fun observeAuditLogs(): Flow<List<VipAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(entry: VipAuditLogEntity)

    @Query("SELECT COUNT(*) FROM vip_audit_logs")
    suspend fun countAuditLogs(): Int
}
