package com.nexo.banca.ui.payments

import com.nexo.banca.data.local.BillEntity
import com.nexo.banca.domain.PaymentType

data class PaymentUiModel(
    val id: String,
    val name: String,
    val amount: Long,
    val status: String,
    val paid: Boolean,
    val type: PaymentType
) {
    companion object {
        fun from(entity: BillEntity) = PaymentUiModel(
            id = entity.id,
            name = entity.name,
            amount = entity.amount,
            status = entity.dueLabel,
            paid = entity.paid,
            type = PaymentType.fromId(entity.id)
        )
    }
}
