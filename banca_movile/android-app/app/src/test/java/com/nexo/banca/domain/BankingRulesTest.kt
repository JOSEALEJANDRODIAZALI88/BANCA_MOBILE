package com.nexo.banca.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
}
