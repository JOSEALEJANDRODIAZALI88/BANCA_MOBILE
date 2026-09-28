package com.nexo.banca.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.banca.data.NexoRepository
import com.nexo.banca.data.OperationResult
import com.nexo.banca.data.local.AccountEntity
import com.nexo.banca.data.local.BillEntity
import com.nexo.banca.data.local.CardSettingsEntity
import com.nexo.banca.data.local.MovementEntity
import com.nexo.banca.data.local.NexoDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NexoUiState(
    val account: AccountEntity? = null,
    val movements: List<MovementEntity> = emptyList(),
    val bills: List<BillEntity> = emptyList(),
    val card: CardSettingsEntity = CardSettingsEntity(locked = false, international = true, contactless = true)
)

class NexoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NexoRepository(NexoDatabase.getInstance(application))

    val state = combine(repository.account, repository.movements, repository.bills, repository.cardSettings) { account, movements, bills, card ->
        NexoUiState(account, movements, bills, card ?: CardSettingsEntity(locked = false, international = true, contactless = true))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NexoUiState())

    init {
        viewModelScope.launch { repository.initialize() }
    }

    fun transfer(recipient: String, account: String, amount: Long, concept: String, complete: (OperationResult) -> Unit) {
        viewModelScope.launch { complete(repository.transfer(recipient, account, amount, concept)) }
    }

    fun payBill(id: String, complete: (OperationResult) -> Unit) {
        viewModelScope.launch { complete(repository.payBill(id)) }
    }

    fun addSavings(complete: (OperationResult) -> Unit) {
        viewModelScope.launch { complete(repository.addSavings(100_000)) }
    }

    fun updateCardLocked(value: Boolean) {
        viewModelScope.launch { repository.updateCardLocked(value) }
    }

    fun updateCardInternational(value: Boolean) {
        viewModelScope.launch { repository.updateCardInternational(value) }
    }

    fun updateCardContactless(value: Boolean) {
        viewModelScope.launch { repository.updateCardContactless(value) }
    }
}
