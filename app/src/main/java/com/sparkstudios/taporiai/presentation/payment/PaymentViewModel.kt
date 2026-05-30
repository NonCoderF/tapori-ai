package com.sparkstudios.taporiai.presentation.payment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sparkstudios.taporiai.repository.TaporiRepository
import com.sparkstudios.taporiai.utils.Prefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentUiState(
    val showLoader: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val newCredits: Int = 0
)

class PaymentViewModel @Inject constructor(
    private val repository: TaporiRepository,
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private var creditsToAdd = 0

    fun setCreditsToAdd(credits: Int) {
        creditsToAdd = credits
    }

    fun handlePaymentSuccess() {
        _uiState.update { it.copy(showLoader = true) }
        viewModelScope.launch {
            try {
                val idToken = Prefs.getUserIdToken(application) ?: ""
                val response = repository.addCredit(idToken, creditsToAdd)
                if (response.isSuccessful) {
                    val credits = response.body()?.credits ?: 0
                    _uiState.update {
                        it.copy(
                            newCredits = credits,
                            showSuccessDialog = true,
                            showLoader = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(showLoader = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(showLoader = false) }
            }
        }
    }

    fun dismissSuccessDialog() {
        _uiState.update { it.copy(showSuccessDialog = false) }
    }
}
