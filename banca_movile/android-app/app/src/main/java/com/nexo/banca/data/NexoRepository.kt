package com.nexo.banca.data

import androidx.room.withTransaction
import com.nexo.banca.data.local.AccountEntity
import com.nexo.banca.data.local.BillEntity
import com.nexo.banca.data.local.CardSettingsEntity
import com.nexo.banca.data.local.MovementEntity
import com.nexo.banca.data.local.NexoDatabase
import com.nexo.banca.domain.BankingRules
import com.nexo.banca.domain.DemoData
import com.nexo.banca.domain.PaymentType
import java.util.UUID

sealed interface OperationResult {
    data class Success(val reference: String) : OperationResult
    data class Error(val message: String) : OperationResult
}

class NexoRepository(private val database: NexoDatabase) {
    private val dao = database.dao()

    val account = dao.observeAccount()
    val movements = dao.observeMovements()
    val bills = dao.observeBills()
    val cardSettings = dao.observeCardSettings()

    suspend fun initialize() = database.withTransaction {
        if (dao.accountCount() == 0) {
            dao.insertAccount(
                AccountEntity(
                    holder = DemoData.ACCOUNT_HOLDER,
                    number = DemoData.ACCOUNT_NUMBER,
                    balance = DemoData.INITIAL_BALANCE,
                    savings = DemoData.INITIAL_SAVINGS
                )
            )
            val now = System.currentTimeMillis()
            dao.insertMovements(
                listOf(
                    MovementEntity(name = "Nomina mensual", category = "Ingreso", timestamp = now, amount = 4_250_000, accent = "lime"),
                    MovementEntity(name = "Mercado Central", category = "Alimentacion", timestamp = now - 86_400_000, amount = -184_600, accent = "coral"),
                    MovementEntity(name = "Fibra Hogar", category = "Servicios", timestamp = now - 172_800_000, amount = -89_900, accent = "blue"),
                    MovementEntity(name = "Transferencia de Sofia", category = "Transferencia", timestamp = now - 259_200_000, amount = 350_000, accent = "purple")
                )
            )
            dao.saveCardSettings(CardSettingsEntity(locked = false, international = true, contactless = true))
        }

        val demoPayments = listOf(
            BillEntity("energy", "Energia del Norte", 128_400, "Vence en 3 dias", "coral", false),
            BillEntity("internet", "Fibra Hogar", 89_900, "Vence en 8 dias", "blue", false),
            BillEntity("mobile", "Plan movil", 54_500, "Pagado este mes", "lime", true),
            BillEntity("water", "Acueducto Municipal", 76_300, "Vence en 12 dias", "blue", false),
            BillEntity("credit-personal", "Credito de libre inversion", 420_000, "Cuota 5 de 24", "purple", false),
            BillEntity("credit-vehicle", "Credito de vehiculo", 685_000, "Cuota 11 de 48", "coral", false)
        )
        val missingPayments = demoPayments.filter { dao.bill(it.id) == null }
        if (missingPayments.isNotEmpty()) dao.insertBills(missingPayments)
    }

    suspend fun transfer(recipient: String, targetAccount: String, amount: Long, concept: String): OperationResult =
        database.withTransaction {
            val account = dao.account() ?: return@withTransaction OperationResult.Error("No se encontro la cuenta local")
            if (recipient.isBlank()) return@withTransaction OperationResult.Error("El destinatario es obligatorio")
            if (!BankingRules.validAccount(targetAccount)) return@withTransaction OperationResult.Error("La cuenta no es valida")
            if (!BankingRules.validAmount(amount, account.balance)) return@withTransaction OperationResult.Error("El valor supera el saldo o el limite permitido")
            dao.updateAccount(account.copy(balance = account.balance - amount))
            dao.insertMovement(
                MovementEntity(
                    name = if (concept.isBlank()) "Transferencia a $recipient" else concept,
                    category = "Transferencia a $recipient",
                    timestamp = System.currentTimeMillis(),
                    amount = -amount,
                    accent = "purple"
                )
            )
            OperationResult.Success(reference())
        }

    suspend fun payBill(id: String): OperationResult = database.withTransaction {
        val bill = dao.bill(id) ?: return@withTransaction OperationResult.Error("La factura no existe")
        if (bill.paid) return@withTransaction OperationResult.Error("La factura ya esta pagada")
        if (bill.amount <= 0) return@withTransaction OperationResult.Error("El valor del pago no es valido")
        val account = dao.account() ?: return@withTransaction OperationResult.Error("No se encontro la cuenta local")
        if (bill.amount > account.balance) return@withTransaction OperationResult.Error("Saldo insuficiente")
        dao.updateAccount(account.copy(balance = account.balance - bill.amount))
        dao.updateBill(bill.copy(paid = true, dueLabel = "Pagado en esta demo"))
        val category = if (PaymentType.fromId(bill.id) == PaymentType.CREDIT) "Pago de credito" else "Pago de servicio"
        dao.insertMovement(MovementEntity(name = bill.name, category = category, timestamp = System.currentTimeMillis(), amount = -bill.amount, accent = bill.accent))
        OperationResult.Success(reference())
    }

    suspend fun addSavings(amount: Long): OperationResult = database.withTransaction {
        val account = dao.account() ?: return@withTransaction OperationResult.Error("No se encontro la cuenta local")
        if (!BankingRules.validSavingsAmount(amount, account.balance)) return@withTransaction OperationResult.Error("El aporte no es valido o supera el saldo")
        dao.updateAccount(account.copy(balance = account.balance - amount, savings = account.savings + amount))
        dao.insertMovement(MovementEntity(name = "Aporte a Viaje a la costa", category = "Ahorro", timestamp = System.currentTimeMillis(), amount = -amount, accent = "blue"))
        OperationResult.Success(reference())
    }

    suspend fun updateCardLocked(value: Boolean) = dao.updateCardLocked(value)

    suspend fun updateCardInternational(value: Boolean) = dao.updateCardInternational(value)

    suspend fun updateCardContactless(value: Boolean) = dao.updateCardContactless(value)

    private fun reference(): String = "NX-${UUID.randomUUID().toString().take(8).uppercase()}"
}
