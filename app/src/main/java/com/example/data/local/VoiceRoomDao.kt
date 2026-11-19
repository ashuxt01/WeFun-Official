package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceRoomSystemDao {

    @Query("SELECT * FROM room_levels ORDER BY level ASC")
    fun observeRoomLevels(): Flow<List<RoomLevelEntity>>

    @Query("SELECT * FROM room_levels ORDER BY level ASC")
    suspend fun getRoomLevels(): List<RoomLevelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoomLevels(levels: List<RoomLevelEntity>)

    @Query("SELECT COUNT(*) FROM room_levels")
    suspend fun countRoomLevels(): Int

    @Query("SELECT * FROM room_seats WHERE roomId = :roomId ORDER BY seatIndex ASC")
    suspend fun getSeatsForRoom(roomId: String): List<RoomSeatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSeats(seats: List<RoomSeatEntity>)

    @Query("SELECT * FROM room_owners WHERE roomId = :roomId LIMIT 1")
    suspend fun getRoomOwner(roomId: String): RoomOwnerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoomOwner(owner: RoomOwnerEntity)

    @Query("SELECT * FROM room_partners WHERE roomId = :roomId LIMIT 1")
    suspend fun getRoomPartner(roomId: String): RoomPartnerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoomPartner(partner: RoomPartnerEntity)

    @Query("SELECT * FROM room_admins WHERE roomId = :roomId")
    suspend fun getRoomAdmins(roomId: String): List<RoomAdminEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoomAdmin(admin: RoomAdminEntity)

    @Query("DELETE FROM room_admins WHERE roomId = :roomId AND userId = :userId")
    suspend fun deleteRoomAdmin(roomId: String, userId: String)

    @Query("SELECT * FROM room_moderators WHERE roomId = :roomId")
    suspend fun getRoomModerators(roomId: String): List<RoomModeratorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoomModerator(mod: RoomModeratorEntity)

    @Query("DELETE FROM room_moderators WHERE roomId = :roomId AND userId = :userId")
    suspend fun deleteRoomModerator(roomId: String, userId: String)

    @Query("SELECT * FROM room_participants WHERE roomId = :roomId")
    suspend fun getParticipants(roomId: String): List<RoomParticipantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertParticipants(participants: List<RoomParticipantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertParticipant(participant: RoomParticipantEntity)

    @Query("SELECT * FROM room_contributions WHERE roomId = :roomId LIMIT 1")
    fun observeRoomContribution(roomId: String): Flow<RoomContributionEntity?>

    @Query("SELECT * FROM room_contributions WHERE roomId = :roomId LIMIT 1")
    suspend fun getRoomContribution(roomId: String): RoomContributionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoomContribution(contribution: RoomContributionEntity)

    @Query("SELECT * FROM room_gift_transactions WHERE roomId = :roomId ORDER BY timestamp DESC LIMIT 100")
    fun observeRoomGiftTransactions(roomId: String): Flow<List<RoomGiftTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRoomGiftTransaction(tx: RoomGiftTransactionEntity)

    @Query("SELECT COUNT(*) FROM room_gift_transactions")
    suspend fun countRoomGiftTransactions(): Int

    @Query("SELECT * FROM contributor_rankings WHERE roomId = :roomId ORDER BY contributionPoints DESC")
    fun observeContributorRankings(roomId: String): Flow<List<ContributorRankingEntity>>

    @Query("SELECT * FROM contributor_rankings WHERE id = :id LIMIT 1")
    suspend fun getRankingById(id: String): ContributorRankingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertContributorRankings(entries: List<ContributorRankingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertContributorRanking(entry: ContributorRankingEntity)

    @Query("SELECT * FROM room_announcements WHERE roomId = :roomId ORDER BY timestamp DESC")
    fun observeAnnouncements(roomId: String): Flow<List<RoomAnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAnnouncements(items: List<RoomAnnouncementEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAnnouncement(item: RoomAnnouncementEntity)

    @Query("SELECT * FROM room_settings WHERE roomId = :roomId LIMIT 1")
    suspend fun getRoomSettings(roomId: String): RoomSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoomSettings(settings: RoomSettingsEntity)

    @Query("SELECT * FROM room_activity_logs WHERE roomId = :roomId ORDER BY timestamp DESC LIMIT 100")
    fun observeActivityLogs(roomId: String): Flow<List<RoomActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: RoomActivityLogEntity)

    @Query("SELECT COUNT(*) FROM room_activity_logs")
    suspend fun countActivityLogs(): Int
}
