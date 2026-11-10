package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MatchStatus
import com.example.data.model.TournamentType
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {

    // --- Tournaments ---
    @Query("SELECT * FROM tournaments ORDER BY matchSchedule ASC")
    fun getAllTournamentsFlow(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE type = :type ORDER BY matchSchedule ASC")
    fun getTournamentsByTypeFlow(type: TournamentType): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
    suspend fun getTournamentById(id: String): TournamentEntity?

    @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
    fun getTournamentByIdFlow(id: String): Flow<TournamentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournaments(tournaments: List<TournamentEntity>)

    @Update
    suspend fun updateTournament(tournament: TournamentEntity)

    @Delete
    suspend fun deleteTournament(tournament: TournamentEntity)

    @Query("DELETE FROM tournaments WHERE id = :id")
    suspend fun deleteTournamentById(id: String)

    @Query("UPDATE tournaments SET filledSlots = :filledSlots WHERE id = :id")
    suspend fun updateFilledSlots(id: String, filledSlots: Int)

    @Query("UPDATE tournaments SET roomId = :roomId, roomPassword = :roomPassword, status = :status WHERE id = :id")
    suspend fun updateRoomCredentials(id: String, roomId: String, roomPassword: String, status: MatchStatus)

    @Query("UPDATE tournaments SET status = :status WHERE id = :id")
    suspend fun updateMatchStatus(id: String, status: MatchStatus)

    // --- Registrations ---
    @Query("SELECT * FROM registrations ORDER BY registeredAt DESC")
    fun getAllRegistrationsFlow(): Flow<List<PlayerRegistrationEntity>>

    @Query("SELECT * FROM registrations WHERE tournamentId = :tournamentId ORDER BY registeredAt ASC")
    fun getRegistrationsForTournamentFlow(tournamentId: String): Flow<List<PlayerRegistrationEntity>>

    @Query("SELECT * FROM registrations WHERE freeFireUid = :freeFireUid ORDER BY registeredAt DESC")
    fun getRegistrationsByUidFlow(freeFireUid: String): Flow<List<PlayerRegistrationEntity>>

    @Query("SELECT * FROM registrations WHERE id = :id LIMIT 1")
    suspend fun getRegistrationById(id: String): PlayerRegistrationEntity?

    @Query("SELECT * FROM registrations WHERE id = :id LIMIT 1")
    fun getRegistrationByIdFlow(id: String): Flow<PlayerRegistrationEntity?>

    @Query("SELECT * FROM registrations WHERE tournamentId = :tournamentId AND freeFireUid = :freeFireUid LIMIT 1")
    suspend fun getRegistrationByTournamentAndUid(tournamentId: String, freeFireUid: String): PlayerRegistrationEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRegistration(registration: PlayerRegistrationEntity): Long

    @Update
    suspend fun updateRegistration(registration: PlayerRegistrationEntity)

    @Delete
    suspend fun deleteRegistration(registration: PlayerRegistrationEntity)

    @Query("SELECT * FROM registrations WHERE registrationStatus = 'PAYMENT_SUBMITTED' ORDER BY registeredAt ASC")
    fun getPendingPaymentVerificationsFlow(): Flow<List<PlayerRegistrationEntity>>

    @Query("SELECT COUNT(*) FROM registrations WHERE tournamentId = :tournamentId AND registrationStatus = 'CONFIRMED'")
    suspend fun getConfirmedRegistrationsCount(tournamentId: String): Int

    @Query("UPDATE registrations SET registrationStatus = 'EXPIRED', paymentStatus = 'REJECTED', rejectionReason = '10-minute payment countdown expired' WHERE registrationStatus = 'AWAITING_PAYMENT' AND paymentExpiresAt < :now")
    suspend fun expireUnpaidRegistrations(now: Long): Int

    // --- Leaderboard ---
    @Query("SELECT * FROM leaderboard ORDER BY totalPoints DESC, kills DESC")
    fun getLeaderboardFlow(): Flow<List<LeaderboardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntity>)

    @Update
    suspend fun updateLeaderboardEntry(entry: LeaderboardEntity)

    // --- Results ---
    @Query("SELECT * FROM tournament_results ORDER BY matchDate DESC")
    fun getAllResultsFlow(): Flow<List<TournamentResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TournamentResultEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<TournamentResultEntity>)

    // --- Admin Settings ---
    @Query("SELECT * FROM admin_settings WHERE id = 1 LIMIT 1")
    fun getAdminSettingsFlow(): Flow<AdminSettingsEntity?>

    @Query("SELECT * FROM admin_settings WHERE id = 1 LIMIT 1")
    suspend fun getAdminSettings(): AdminSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAdminSettings(settings: AdminSettingsEntity)
}
