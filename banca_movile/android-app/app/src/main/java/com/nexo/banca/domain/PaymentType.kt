package com.nexo.banca.domain

enum class PaymentType {
    SERVICE,
    CREDIT;

    companion object {
        fun fromId(id: String): PaymentType = if (id.startsWith("credit-")) CREDIT else SERVICE
    }
}
