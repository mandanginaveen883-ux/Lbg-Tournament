package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RegistrationStatus
import com.example.data.model.TournamentType
import com.example.data.repository.TournamentRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches LBG ARMY TOURNAMENT`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("LBG ARMY TOURNAMENT", appName)
    }

    @Test
    fun `test player registration and duplicate prevention`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = TournamentRepository(context)

        // 1. Register player
        val result1 = repo.registerPlayer(
            tournamentId = "tour_free_solo_01",
            playerName = "Naveen",
            freeFireUid = "1049281729",
            inGameName = "LBG_NAVEEN⚡",
            mobileNumber = "9876543210"
        )
        assertTrue(result1 is TournamentRepository.RegistrationResult.Success)

        // 2. Duplicate registration with same Free Fire UID should fail
        val result2 = repo.registerPlayer(
            tournamentId = "tour_free_solo_01",
            playerName = "Naveen Duplicate",
            freeFireUid = "1049281729",
            inGameName = "LBG_NAVEEN⚡",
            mobileNumber = "9876543210"
        )
        assertTrue(result2 is TournamentRepository.RegistrationResult.DuplicateError)
    }

    @Test
    fun `test paid registration and UTR submission with admin approval`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = TournamentRepository(context)

        // 1. Register for paid tournament (₹15)
        val result = repo.registerPlayer(
            tournamentId = "tour_paid_solo_15",
            playerName = "Vikram",
            freeFireUid = "1928374650",
            inGameName = "VIKRAM_PRO",
            mobileNumber = "9123456780"
        )
        assertTrue(result is TournamentRepository.RegistrationResult.Success)
        val reg = (result as TournamentRepository.RegistrationResult.Success).registration
        assertEquals(RegistrationStatus.AWAITING_PAYMENT, reg.registrationStatus)

        // 2. Submit UTR payment proof
        val utrSuccess = repo.submitPaymentUtr(reg.id, "431098273612", "Paid via PhonePe")
        assertTrue(utrSuccess)

        // 3. Admin verifies and approves payment
        val approveSuccess = repo.adminApprovePayment(reg.id)
        assertTrue(approveSuccess)
    }
}
