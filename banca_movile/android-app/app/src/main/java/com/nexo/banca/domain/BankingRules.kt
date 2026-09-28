package com.nexo.banca.domain

object BankingRules {
    const val MAX_TRANSFER = 5_000_000L
    const val DEMO_USER = "demo@nexo.com"
    const val DEMO_PASSWORD = "Nexo2026"

    fun validCredentials(user: String, password: String): Boolean =
        user.trim().equals(DEMO_USER, ignoreCase = true) && password == DEMO_PASSWORD

    fun validAccount(account: String): Boolean = account.length in 6..20 && account.all(Char::isDigit)

    fun validAmount(amount: Long, balance: Long): Boolean =
        amount in 1..MAX_TRANSFER && amount <= balance

    fun validSavingsAmount(amount: Long, balance: Long): Boolean = amount > 0 && amount <= balance
}
