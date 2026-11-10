package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AdminSettingsEntity
import com.example.data.local.AppDatabase
import com.example.data.local.LeaderboardEntity
import com.example.data.local.PlayerRegistrationEntity
import com.example.data.local.TournamentEntity
import com.example.data.local.TournamentResultEntity
import com.example.data.model.MatchMode
import com.example.data.model.MatchStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.RegistrationStatus
import com.example.data.model.TournamentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class TournamentRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val dao = db.tournamentDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("lbg_player_prefs", Context.MODE_PRIVATE)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeDatabaseIfNeeded()
        }
    }

    private suspend fun initializeDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        val existing = dao.getAllTournamentsFlow().firstOrNull()
        if (existing.isNullOrEmpty()) {
            dao.insertTournaments(SampleData.getInitialTournaments())
            dao.insertLeaderboardEntries(SampleData.getInitialLeaderboard())
            dao.insertResults(SampleData.getInitialResults())
            dao.insertOrUpdateAdminSettings(SampleData.getInitialAdminSettings())
        }
    }

    // --- Saved Player Profile (Fast Auto-fill) ---
    fun getSavedPlayerName(): String = prefs.getString("saved_player_name", "") ?: ""
    fun getSavedFfUid(): String = prefs.getString("saved_ff_uid", "") ?: ""
    fun getSavedIgn(): String = prefs.getString("saved_ign", "") ?: ""
    fun getSavedMobile(): String = prefs.getString("saved_mobile", "") ?: ""

    fun savePlayerProfile(name: String, uid: String, ign: String, mobile: String) {
        prefs.edit()
            .putString("saved_player_name", name)
            .putString("saved_ff_uid", uid)
            .putString("saved_ign", ign)
            .putString("saved_mobile", mobile)
            .apply()
    }

    // --- Tournaments Flow & Queries ---
    val allTournaments: Flow<List<TournamentEntity>> = dao.getAllTournamentsFlow()
    fun getTournamentsByType(type: TournamentType): Flow<List<TournamentEntity>> = dao.getTournamentsByTypeFlow(type)
    fun getTournamentByIdFlow(id: String): Flow<TournamentEntity?> = dao.getTournamentByIdFlow(id)
    suspend fun getTournamentById(id: String): TournamentEntity? = dao.getTournamentById(id)

    // --- Registrations Flow & Queries ---
    val allRegistrations: Flow<List<PlayerRegistrationEntity>> = dao.getAllRegistrationsFlow()
    val pendingVerifications: Flow<List<PlayerRegistrationEntity>> = dao.getPendingPaymentVerificationsFlow()

    fun getRegistrationsForTournament(tournamentId: String): Flow<List<PlayerRegistrationEntity>> =
        dao.getRegistrationsForTournamentFlow(tournamentId)

    fun getRegistrationsByUid(freeFireUid: String): Flow<List<PlayerRegistrationEntity>> =
        dao.getRegistrationsByUidFlow(freeFireUid)

    fun getRegistrationByIdFlow(id: String): Flow<PlayerRegistrationEntity?> = dao.getRegistrationByIdFlow(id)

    // --- Leaderboard & Results & Settings Flow ---
    val leaderboard: Flow<List<LeaderboardEntity>> = dao.getLeaderboardFlow()
    val tournamentResults: Flow<List<TournamentResultEntity>> = dao.getAllResultsFlow()
    val adminSettings: Flow<AdminSettingsEntity?> = dao.getAdminSettingsFlow()

    // --- Expiry Check Routine ---
    suspend fun checkAndExpireUnpaid() = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.expireUnpaidRegistrations(now)
    }

    // --- Player Registration Result Sealed Class ---
    sealed class RegistrationResult {
        data class Success(val registration: PlayerRegistrationEntity) : RegistrationResult()
        data class DuplicateError(val message: String) : RegistrationResult()
        data class TournamentFullError(val message: String) : RegistrationResult()
        data class Error(val message: String) : RegistrationResult()
    }

    suspend fun registerPlayer(
        tournamentId: String,
        playerName: String,
        freeFireUid: String,
        inGameName: String,
        mobileNumber: String,
        teamName: String? = null,
        teammateUids: String? = null
    ): RegistrationResult = withContext(Dispatchers.IO) {
        val tournament = dao.getTournamentById(tournamentId)
            ?: return@withContext RegistrationResult.Error("Tournament not found")

        // 1. Prevent duplicate registration
        val existing = dao.getRegistrationByTournamentAndUid(tournamentId, freeFireUid.trim())
        if (existing != null && existing.registrationStatus != RegistrationStatus.EXPIRED && existing.registrationStatus != RegistrationStatus.REJECTED) {
            return@withContext RegistrationResult.DuplicateError(
                "Free Fire UID $freeFireUid is already registered for this tournament (Status: ${existing.registrationStatus.name})"
            )
        }

        // 2. Check slots availability
        if (tournament.filledSlots >= tournament.totalSlots) {
            return@withContext RegistrationResult.TournamentFullError("This tournament is already full (${tournament.filledSlots}/${tournament.totalSlots})")
        }

        // 3. Save profile for user convenience
        savePlayerProfile(playerName, freeFireUid, inGameName, mobileNumber)

        val regId = "reg_" + UUID.randomUUID().toString().take(8)
        val isFree = tournament.type == TournamentType.FREE || tournament.entryFee == 0
        val assignedSlot = tournament.filledSlots + 1

        val registration = if (isFree) {
            PlayerRegistrationEntity(
                id = regId,
                tournamentId = tournament.id,
                tournamentTitle = tournament.title,
                playerName = playerName.trim(),
                freeFireUid = freeFireUid.trim(),
                inGameName = inGameName.trim(),
                mobileNumber = mobileNumber.trim(),
                teamName = teamName?.trim(),
                teammateUids = teammateUids?.trim(),
                entryFee = 0,
                registrationStatus = RegistrationStatus.CONFIRMED,
                paymentStatus = PaymentStatus.FREE_ENTRY,
                assignedSlotNumber = assignedSlot,
                registeredAt = System.currentTimeMillis(),
                verifiedAt = System.currentTimeMillis()
            )
        } else {
            PlayerRegistrationEntity(
                id = regId,
                tournamentId = tournament.id,
                tournamentTitle = tournament.title,
                playerName = playerName.trim(),
                freeFireUid = freeFireUid.trim(),
                inGameName = inGameName.trim(),
                mobileNumber = mobileNumber.trim(),
                teamName = teamName?.trim(),
                teammateUids = teammateUids?.trim(),
                entryFee = tournament.entryFee,
                registrationStatus = RegistrationStatus.AWAITING_PAYMENT,
                paymentStatus = PaymentStatus.UNPAID,
                paymentExpiresAt = System.currentTimeMillis() + 10 * 60 * 1000L, // 10 minutes
                assignedSlotNumber = assignedSlot,
                registeredAt = System.currentTimeMillis()
            )
        }

        try {
            dao.insertRegistration(registration)
            dao.updateFilledSlots(tournament.id, tournament.filledSlots + 1)
            RegistrationResult.Success(registration)
        } catch (e: Exception) {
            RegistrationResult.Error("Registration failed: ${e.localizedMessage}")
        }
    }

    // --- Submit Payment Proof (UTR) ---
    suspend fun submitPaymentUtr(
        registrationId: String,
        utrNumber: String,
        note: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val reg = dao.getRegistrationById(registrationId) ?: return@withContext false
        if (System.currentTimeMillis() > reg.paymentExpiresAt) {
            // Already expired
            dao.updateRegistration(
                reg.copy(
                    registrationStatus = RegistrationStatus.EXPIRED,
                    paymentStatus = PaymentStatus.REJECTED,
                    rejectionReason = "Payment window expired before UTR submission"
                )
            )
            return@withContext false
        }

        dao.updateRegistration(
            reg.copy(
                utrNumber = utrNumber.trim(),
                paymentProofNote = note?.trim(),
                registrationStatus = RegistrationStatus.PAYMENT_SUBMITTED,
                paymentStatus = PaymentStatus.VERIFICATION_PENDING
            )
        )
        true
    }

    // --- Test Sandbox Payment (Instant Mock Verification for testing/evaluating) ---
    suspend fun submitTestPayment(registrationId: String): Boolean = withContext(Dispatchers.IO) {
        val reg = dao.getRegistrationById(registrationId) ?: return@withContext false
        dao.updateRegistration(
            reg.copy(
                utrNumber = "TEST_UTR_" + System.currentTimeMillis().toString().takeLast(6),
                paymentProofNote = "Test payment auto-verified in Sandbox Mode",
                registrationStatus = RegistrationStatus.CONFIRMED,
                paymentStatus = PaymentStatus.VERIFIED_PAID,
                isTestPayment = true,
                verifiedAt = System.currentTimeMillis()
            )
        )
        true
    }

    // --- Admin Verification of Payment Proof ---
    suspend fun adminApprovePayment(registrationId: String): Boolean = withContext(Dispatchers.IO) {
        val reg = dao.getRegistrationById(registrationId) ?: return@withContext false
        dao.updateRegistration(
            reg.copy(
                registrationStatus = RegistrationStatus.CONFIRMED,
                paymentStatus = PaymentStatus.VERIFIED_PAID,
                verifiedAt = System.currentTimeMillis(),
                rejectionReason = null
            )
        )
        true
    }

    suspend fun adminRejectPayment(registrationId: String, reason: String): Boolean = withContext(Dispatchers.IO) {
        val reg = dao.getRegistrationById(registrationId) ?: return@withContext false
        dao.updateRegistration(
            reg.copy(
                registrationStatus = RegistrationStatus.REJECTED,
                paymentStatus = PaymentStatus.REJECTED,
                rejectionReason = reason,
                verifiedAt = System.currentTimeMillis()
            )
        )
        // Release slot if rejected
        val tournament = dao.getTournamentById(reg.tournamentId)
        if (tournament != null && tournament.filledSlots > 0) {
            dao.updateFilledSlots(tournament.id, tournament.filledSlots - 1)
        }
        true
    }

    // --- Admin Tournament Management ---
    suspend fun createTournament(
        title: String,
        type: TournamentType,
        mode: MatchMode,
        entryFee: Int,
        totalSlots: Int,
        mapName: String,
        matchSchedule: Long,
        firstPrize: Int,
        secondPrize: Int,
        thirdPrize: Int,
        perKillBonus: Int,
        totalPrizePool: Int,
        rules: String
    ): String = withContext(Dispatchers.IO) {
        val id = "tour_" + UUID.randomUUID().toString().take(8)
        val entity = TournamentEntity(
            id = id,
            title = title.trim(),
            type = type,
            mode = mode,
            entryFee = entryFee,
            totalSlots = totalSlots,
            filledSlots = 0,
            mapName = mapName.trim(),
            matchSchedule = matchSchedule,
            firstPrize = firstPrize,
            secondPrize = secondPrize,
            thirdPrize = thirdPrize,
            perKillBonus = perKillBonus,
            totalPrizePool = totalPrizePool,
            rules = rules.trim()
        )
        dao.insertTournament(entity)
        id
    }

    suspend fun updateTournament(tournament: TournamentEntity) = withContext(Dispatchers.IO) {
        dao.updateTournament(tournament)
    }

    suspend fun deleteTournament(id: String) = withContext(Dispatchers.IO) {
        dao.deleteTournamentById(id)
    }

    suspend fun updateRoomCredentials(
        tournamentId: String,
        roomId: String,
        roomPass: String,
        status: MatchStatus
    ) = withContext(Dispatchers.IO) {
        dao.updateRoomCredentials(tournamentId, roomId.trim(), roomPass.trim(), status)
    }

    suspend fun updateMatchStatus(tournamentId: String, status: MatchStatus) = withContext(Dispatchers.IO) {
        dao.updateMatchStatus(tournamentId, status)
    }

    // --- Declare Tournament Result & Winners ---
    suspend fun declareTournamentResult(
        tournamentId: String,
        winnerIgn: String,
        winnerUid: String,
        winnerPrize: Int,
        runnerUpIgn: String,
        runnerUpPrize: Int,
        thirdPlaceIgn: String,
        thirdPlacePrize: Int,
        mvpIgn: String,
        mvpKills: Int,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val tournament = dao.getTournamentById(tournamentId) ?: return@withContext
        val result = TournamentResultEntity(
            id = "res_" + UUID.randomUUID().toString().take(8),
            tournamentId = tournamentId,
            tournamentTitle = tournament.title,
            matchDate = System.currentTimeMillis(),
            mapName = tournament.mapName,
            winnerIgn = winnerIgn.trim(),
            winnerUid = winnerUid.trim(),
            winnerPrize = winnerPrize,
            runnerUpIgn = runnerUpIgn.trim(),
            runnerUpPrize = runnerUpPrize,
            thirdPlaceIgn = thirdPlaceIgn.trim(),
            thirdPlacePrize = thirdPlacePrize,
            mvpIgn = mvpIgn.trim(),
            mvpKills = mvpKills,
            summaryNotes = notes.trim()
        )
        dao.insertResult(result)
        dao.updateMatchStatus(tournamentId, MatchStatus.COMPLETED)
    }

    // --- Admin Settings ---
    suspend fun updateAdminSettings(settings: AdminSettingsEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateAdminSettings(settings)
    }

    suspend fun getAdminSettings(): AdminSettingsEntity = withContext(Dispatchers.IO) {
        dao.getAdminSettings() ?: SampleData.getInitialAdminSettings()
    }
}
