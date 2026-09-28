package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.TenancyAgreement
import com.example.data.model.TenancyAgreementStatus
import com.example.data.repository.PropertyRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Dalalion Pocket", appName)
  }

  @Test
  fun `verify dop 50 percent commission rule and mutual acceptance locking`() {
    val agreements = PropertyRepository.tenancyAgreements.value
    assertTrue("Agreements should not be empty", agreements.isNotEmpty())
    val agreement = agreements.first()

    // Business rule: DoP takes 50% of the first month's rent only
    val expectedDopFee = agreement.monthlyRentTzs / 2
    assertEquals(expectedDopFee, agreement.dopPlatformFeeTzs)

    // Verify mutual acceptance flow
    PropertyRepository.customerAcceptAgreement(agreement.agreementCode)
    PropertyRepository.ownerAcceptAgreement(agreement.agreementCode)

    val updatedAgreement = PropertyRepository.tenancyAgreements.value.first { it.agreementCode == agreement.agreementCode }
    assertEquals(TenancyAgreementStatus.LOCKED, updatedAgreement.status)
    assertTrue("Agreement should have locked timestamp", updatedAgreement.lockedAt != null)
  }
}
