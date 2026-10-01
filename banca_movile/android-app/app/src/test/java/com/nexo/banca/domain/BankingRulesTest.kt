package com.nexo.banca.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class BankingRulesTest {
    @Test
    fun credentialsAcceptOnlyDemoAccount() {
        assertTrue(BankingRules.validCredentials(" DEMO@nexo.com ", "Nexo2026"))
        assertFalse(BankingRules.validCredentials("demo@nexo.com", "incorrecta"))
    }

    @Test
    fun accountRequiresSixToTwentyDigits() {
        assertTrue(BankingRules.validAccount("123456"))
        assertTrue(BankingRules.validAccount("12345678901234567890"))
        assertFalse(BankingRules.validAccount("12345"))
        assertFalse(BankingRules.validAccount("ABC123456"))
    }

    @Test
    fun transferMustBePositiveWithinBalanceAndLimit() {
        assertTrue(BankingRules.validAmount(1, 1))
        assertTrue(BankingRules.validAmount(BankingRules.MAX_TRANSFER, BankingRules.MAX_TRANSFER))
        assertFalse(BankingRules.validAmount(0, 1_000))
        assertFalse(BankingRules.validAmount(1_001, 1_000))
        assertFalse(BankingRules.validAmount(BankingRules.MAX_TRANSFER + 1, Long.MAX_VALUE))
    }

    @Test
    fun savingsMustBePositiveAndAvailable() {
        assertTrue(BankingRules.validSavingsAmount(100_000, 100_000))
        assertFalse(BankingRules.validSavingsAmount(0, 100_000))
        assertFalse(BankingRules.validSavingsAmount(100_001, 100_000))
    }

    @Test
    fun loanCalculatorHandlesZeroAndPositiveRates() {
        val zeroRate = LoanCalculator.estimate(12_000_000, 0.0, 12)
        assertEquals(1_000_000L, zeroRate?.monthlyPayment)
        assertEquals(12_000_000L, zeroRate?.totalPayment)
        assertEquals(0L, zeroRate?.totalInterest)

        val financed = LoanCalculator.estimate(12_000_000, 20.0, 24)
        assertTrue(financed != null && financed.monthlyPayment > 0)
        assertTrue(financed != null && financed.totalPayment > 12_000_000)
    }

    @Test
    fun loanCalculatorRejectsOutOfRangeInputs() {
        assertNull(LoanCalculator.estimate(0, 20.0, 12))
        assertNull(LoanCalculator.estimate(1_000_000, -1.0, 12))
        assertNull(LoanCalculator.estimate(1_000_000, 20.0, 0))
        assertNull(LoanCalculator.estimate(LoanCalculator.MAX_PRINCIPAL + 1, 20.0, 12))
    }

    @Test
    fun loanTotalNeverFallsBelowPrincipalAfterRounding() {
        val estimate = LoanCalculator.estimate(10, 0.0, 3)

        assertEquals(3L, estimate?.monthlyPayment)
        assertEquals(10L, estimate?.totalPayment)
        assertEquals(0L, estimate?.totalInterest)
    }

    @Test
    fun paymentTypeUsesOneLegacyIdRule() {
        assertEquals(PaymentType.CREDIT, PaymentType.fromId("credit-personal"))
        assertEquals(PaymentType.SERVICE, PaymentType.fromId("energy"))
    }
}
