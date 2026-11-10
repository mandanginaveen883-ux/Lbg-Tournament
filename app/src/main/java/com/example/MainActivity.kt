package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RegistrationStatus
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.ContactAdminScreen
import com.example.ui.screens.FreeTournamentsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MyMatchesScreen
import com.example.ui.screens.PaidTournamentsScreen
import com.example.ui.screens.PaymentGatewayScreen
import com.example.ui.screens.PlayerRegistrationScreen
import com.example.ui.screens.TournamentDetailScreen
import com.example.ui.screens.TournamentResultsScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EsportsRed
import com.example.ui.theme.GamingBlack
import com.example.ui.theme.GamingCardSurface
import com.example.ui.theme.LbgArmyTournamentTheme
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TournamentGold
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TournamentViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: TournamentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LbgArmyTournamentTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: TournamentViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val allRegistrations by viewModel.allRegistrations.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val activeCount = remember(allRegistrations) {
        allRegistrations.count {
            it.registrationStatus == RegistrationStatus.CONFIRMED ||
                    it.registrationStatus == RegistrationStatus.PAYMENT_SUBMITTED ||
                    it.registrationStatus == RegistrationStatus.AWAITING_PAYMENT
        }
    }

    // Collect UI messages
    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Android Hardware Back Button Handling
    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Show Bottom Navigation only on primary tabs
            val isPrimaryTab = currentScreen is Screen.Home ||
                    currentScreen is Screen.FreeTournaments ||
                    currentScreen is Screen.PaidTournaments ||
                    currentScreen is Screen.MyMatches ||
                    currentScreen is Screen.Leaderboard

            if (isPrimaryTab) {
                GamerBottomBar(
                    currentScreen = currentScreen,
                    activeMatchesCount = activeCount,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GamingBlack)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    (slideInHorizontally { width -> width / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { width -> -width / 3 } + fadeOut())
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    is Screen.Home -> HomeScreen(viewModel = viewModel)
                    is Screen.FreeTournaments -> FreeTournamentsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    is Screen.PaidTournaments -> PaidTournamentsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    is Screen.TournamentDetail -> TournamentDetailScreen(
                        tournamentId = screen.tournamentId,
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    is Screen.PlayerRegistration -> PlayerRegistrationScreen(
                        tournamentId = screen.tournamentId,
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    is Screen.PaymentGateway -> PaymentGatewayScreen(
                        registrationId = screen.registrationId,
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    is Screen.MyMatches -> MyMatchesScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    is Screen.Leaderboard -> LeaderboardScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    is Screen.Results -> TournamentResultsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    is Screen.ContactAdmin -> ContactAdminScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                    is Screen.AdminPanel -> AdminPanelScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }
}

@Composable
fun GamerBottomBar(
    currentScreen: Screen,
    activeMatchesCount: Int,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = GamingCardSurface,
        contentColor = TextPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // 1. Home
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = gamerNavColors(),
            modifier = Modifier.testTag("nav_home")
        )

        // 2. Free Tournaments
        NavigationBarItem(
            selected = currentScreen is Screen.FreeTournaments,
            onClick = { onNavigate(Screen.FreeTournaments) },
            icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Free") },
            label = { Text("Free", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = gamerNavColors(),
            modifier = Modifier.testTag("nav_free")
        )

        // 3. Paid Tournaments
        NavigationBarItem(
            selected = currentScreen is Screen.PaidTournaments,
            onClick = { onNavigate(Screen.PaidTournaments) },
            icon = { Icon(Icons.Default.Payments, contentDescription = "Paid") },
            label = { Text("Paid", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = gamerNavColors(),
            modifier = Modifier.testTag("nav_paid")
        )

        // 4. My Matches
        NavigationBarItem(
            selected = currentScreen is Screen.MyMatches,
            onClick = { onNavigate(Screen.MyMatches) },
            icon = {
                if (activeMatchesCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = EsportsRed, contentColor = Color.White) {
                                Text("$activeMatchesCount")
                            }
                        }
                    ) {
                        Icon(Icons.Default.CardMembership, contentDescription = "My Matches")
                    }
                } else {
                    Icon(Icons.Default.CardMembership, contentDescription = "My Matches")
                }
            },
            label = { Text("Matches", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = gamerNavColors(),
            modifier = Modifier.testTag("nav_my_matches")
        )

        // 5. Leaderboard
        NavigationBarItem(
            selected = currentScreen is Screen.Leaderboard,
            onClick = { onNavigate(Screen.Leaderboard) },
            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Leaderboard") },
            label = { Text("Ranks", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = gamerNavColors(),
            modifier = Modifier.testTag("nav_leaderboard")
        )
    }
}

@Composable
private fun gamerNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = EsportsRed,
    selectedTextColor = EsportsRed,
    unselectedIconColor = TextMuted,
    unselectedTextColor = TextMuted,
    indicatorColor = EsportsRed.copy(alpha = 0.15f)
)
