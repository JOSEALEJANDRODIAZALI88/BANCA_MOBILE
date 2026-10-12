package com.nexo.banca.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.banca.data.NexoRepository
import com.nexo.banca.data.OperationResult
import com.nexo.banca.data.local.AccountEntity
import com.nexo.banca.data.local.CardSettingsEntity
import com.nexo.banca.data.local.MovementEntity
import com.nexo.banca.data.local.NexoDatabase
import com.nexo.banca.data.preferences.ThemeMode
import com.nexo.banca.data.preferences.UserPreferences
import com.nexo.banca.data.preferences.UserPreferencesRepository
import com.nexo.banca.ui.payments.PaymentUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NexoUiState(
    val account: AccountEntity? = null,
    val movements: List<MovementEntity> = emptyList(),
    val payments: List<PaymentUiModel> = emptyList(),
    val card: CardSettingsEntity = CardSettingsEntity(locked = false, international = true, contactless = true),
    val preferences: UserPreferences = UserPreferences()
)

class NexoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NexoRepository(NexoDatabase.getInstance(application))
    private val preferencesRepository = UserPreferencesRepository(application)

    val state = combine(repository.account, repository.movements, repository.bills, repository.cardSettings, preferencesRepository.preferences) { account, movements, bills, card, preferences ->
        NexoUiState(account, movements, bills.map(PaymentUiModel::from), card ?: CardSettingsEntity(locked = false, international = true, contactless = true), preferences)
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

    fun setBiometricOnLaunch(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setBiometricOnLaunch(enabled) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferencesRepository.setThemeMode(mode) }
    }

    fun setShowBalanceOnHome(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setShowBalanceOnHome(enabled) }
    }

    fun resetDemo(complete: (String) -> Unit) {
        viewModelScope.launch {
            val message = runCatching {
                repository.resetDemo()
                preferencesRepository.reset()
            }.fold(
                onSuccess = { "Demostracion restablecida" },
                onFailure = { "No fue posible restablecer la demostracion" }
            )
            complete(message)
        }
    }
}
