package com.nexo.banca.domain

import kotlin.math.pow
import kotlin.math.roundToLong

data class LoanEstimate(
    val monthlyPayment: Long,
    val totalPayment: Long,
    val totalInterest: Long
)

object LoanCalculator {
    const val MAX_PRINCIPAL = 1_000_000_000L
    const val MAX_MONTHS = 360
    const val MAX_ANNUAL_EFFECTIVE_RATE = 100.0

    fun estimate(principal: Long, annualEffectiveRate: Double, months: Int): LoanEstimate? {
        if (principal !in 1..MAX_PRINCIPAL ||
            !annualEffectiveRate.isFinite() ||
            annualEffectiveRate !in 0.0..MAX_ANNUAL_EFFECTIVE_RATE ||
            months !in 1..MAX_MONTHS
        ) return null

        val monthlyRate = (1 + annualEffectiveRate / 100).pow(1.0 / 12) - 1
        val unroundedPayment = if (monthlyRate == 0.0) {
            principal.toDouble() / months
        } else {
            val factor = (1 + monthlyRate).pow(months)
            principal * monthlyRate * factor / (factor - 1)
        }
        val payment = unroundedPayment.roundToLong()
        val total = if (monthlyRate == 0.0) principal else (unroundedPayment * months).roundToLong().coerceAtLeast(principal)
        return LoanEstimate(payment, total, (total - principal).coerceAtLeast(0))
    }
}
