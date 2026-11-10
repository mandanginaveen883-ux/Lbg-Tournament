package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AdminSettingsEntity
import com.example.data.local.LeaderboardEntity
import com.example.data.local.PlayerRegistrationEntity
import com.example.data.local.TournamentEntity
import com.example.data.local.TournamentResultEntity
import com.example.data.model.MatchMode
import com.example.data.model.MatchStatus
import com.example.data.model.RegistrationStatus
import com.example.data.model.TournamentType
import com.example.data.repository.TournamentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Home : Screen()
    data object FreeTournaments : Screen()
    data object PaidTournaments : Screen()
    data class TournamentDetail(val tournamentId: String) : Screen()
    data class PlayerRegistration(val tournamentId: String) : Screen()
    data class PaymentGateway(val registrationId: String) : Screen()
    data object MyMatches : Screen()
    data object Leaderboard : Screen()
    data object Results : Screen()
    data object ContactAdmin : Screen()
    data object AdminPanel : Screen()
}

class TournamentViewModel(application: Application) : AndroidViewModel(application) {

    val repository = TournamentRepository(application)

    // Navigation Stack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = MutableStateFlow<Screen>(Screen.Home).apply {
        viewModelScope.launch {
            _screenStack.collect { stack ->
                value = stack.lastOrNull() ?: Screen.Home
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _screenStack.value = _screenStack.value + screen
    }

    fun navigateBack(): Boolean {
        if (_screenStack.value.size > 1) {
            _screenStack.value = _screenStack.value.dropLast(1)
            return true
        }
        return false
    }

    // Live Data Flows
    val tournaments: StateFlow<List<TournamentEntity>> = repository.allTournaments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRegistrations: StateFlow<List<PlayerRegistrationEntity>> = repository.allRegistrations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingVerifications: StateFlow<List<PlayerRegistrationEntity>> = repository.pendingVerifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaderboard: StateFlow<List<LeaderboardEntity>> = repository.leaderboard
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val results: StateFlow<List<TournamentResultEntity>> = repository.tournamentResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminSettings: StateFlow<AdminSettingsEntity?> = repository.adminSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Message Events
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Admin Auth State
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    // Active Current Player Free Fire UID (for My Matches)
    private val _activePlayerUid = MutableStateFlow(repository.getSavedFfUid())
    val activePlayerUid: StateFlow<String> = _activePlayerUid.asStateFlow()

    // 10-Minute Countdown Clock in ms for payment screen
    private val _currentTimeMs = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMs: StateFlow<Long> = _currentTimeMs.asStateFlow()

    init {
        // Periodic ticker for live countdowns & unpaid expiration
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _currentTimeMs.value = System.currentTimeMillis()
                // Check for expired payments every 10 seconds
                if ((_currentTimeMs.value / 1000) % 10 == 0L) {
                    repository.checkAndExpireUnpaid()
                }
            }
        }
    }

    fun showMessage(msg: String) {
        viewModelScope.launch {
            _userMessage.emit(msg)
        }
    }

    // --- Admin Login ---
    fun loginAdmin(pin: String): Boolean {
        val currentSettings = adminSettings.value
        val correctPin = currentSettings?.adminPin ?: "7788"
        return if (pin.trim() == correctPin) {
            _isAdminAuthenticated.value = true
            showMessage("Admin mode activated")
            true
        } else {
            showMessage("Incorrect Admin PIN! Default is 7788")
            false
        }
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
        showMessage("Logged out of Admin mode")
    }

    // --- Player Registration Action ---
    fun registerPlayer(
        tournamentId: String,
        playerName: String,
        freeFireUid: String,
        inGameName: String,
        mobileNumber: String,
        teamName: String? = null,
        teammateUids: String? = null,
        onSuccess: (PlayerRegistrationEntity) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerPlayer(
                tournamentId = tournamentId,
                playerName = playerName,
                freeFireUid = freeFireUid,
                inGameName = inGameName,
                mobileNumber = mobileNumber,
                teamName = teamName,
                teammateUids = teammateUids
            )
            when (result) {
                is TournamentRepository.RegistrationResult.Success -> {
                    _activePlayerUid.value = freeFireUid
                    if (result.registration.registrationStatus == RegistrationStatus.CONFIRMED) {
                        showMessage("Registration Confirmed! Slot #${result.registration.assignedSlotNumber}")
                    } else {
                        showMessage("Slot reserved! Please complete payment within 10 minutes.")
                    }
                    onSuccess(result.registration)
                }
                is TournamentRepository.RegistrationResult.DuplicateError -> {
                    showMessage(result.message)
                }
                is TournamentRepository.RegistrationResult.TournamentFullError -> {
                    showMessage(result.message)
                }
                is TournamentRepository.RegistrationResult.Error -> {
                    showMessage(result.message)
                }
            }
        }
    }

    // --- Submit UTR Payment Proof ---
    fun submitPaymentUtr(registrationId: String, utr: String, note: String?, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.submitPaymentUtr(registrationId, utr, note)
            if (success) {
                showMessage("UTR submitted! Awaiting Admin verification.")
            } else {
                showMessage("Payment window has expired. Please register again.")
            }
            onDone(success)
        }
    }

    // --- Sandbox Test Payment ---
    fun submitTestPayment(registrationId: String, onDone: () -> Unit) {
        viewModelScope.launch {
            val success = repository.submitTestPayment(registrationId)
            if (success) {
                showMessage("⚡ Test payment verified instantly in Sandbox Mode!")
            }
            onDone()
        }
    }

    // --- Admin Verification Actions ---
    fun approvePayment(registrationId: String) {
        viewModelScope.launch {
            repository.adminApprovePayment(registrationId)
            showMessage("Payment approved! Slot confirmed for player.")
        }
    }

    fun rejectPayment(registrationId: String, reason: String) {
        viewModelScope.launch {
            repository.adminRejectPayment(registrationId, reason)
            showMessage("Payment rejected: $reason")
        }
    }

    // --- Admin Tournament Actions ---
    fun createTournament(
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
        rules: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createTournament(
                title, type, mode, entryFee, totalSlots, mapName,
                matchSchedule, firstPrize, secondPrize, thirdPrize,
                perKillBonus, totalPrizePool, rules
            )
            showMessage("Tournament created successfully!")
            onDone()
        }
    }

    fun updateTournament(tournament: TournamentEntity) {
        viewModelScope.launch {
            repository.updateTournament(tournament)
            showMessage("Tournament updated")
        }
    }

    fun deleteTournament(id: String) {
        viewModelScope.launch {
            repository.deleteTournament(id)
            showMessage("Tournament deleted")
        }
    }

    fun updateRoomCredentials(tournamentId: String, roomId: String, roomPass: String, status: MatchStatus) {
        viewModelScope.launch {
            repository.updateRoomCredentials(tournamentId, roomId, roomPass, status)
            showMessage("Room ID & Password updated for players!")
        }
    }

    fun declareTournamentResult(
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
        notes: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.declareTournamentResult(
                tournamentId, winnerIgn, winnerUid, winnerPrize,
                runnerUpIgn, runnerUpPrize, thirdPlaceIgn, thirdPlacePrize,
                mvpIgn, mvpKills, notes
            )
            showMessage("Winners declared and recorded!")
            onDone()
        }
    }

    fun updateAdminSettings(settings: AdminSettingsEntity) {
        viewModelScope.launch {
            repository.updateAdminSettings(settings)
            showMessage("Admin settings saved")
        }
    }
}
