package com.pepsigo.admin.screens.printInvoice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.repository.SalePrintRepo
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrintInvoiceUiState(
    val saleId: Int = 0,
    val invoiceText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
class PrintInvoiceViewModel(
    private val salePrintRepo: SalePrintRepo,
    private val saleId: Int
) : ViewModel() {

    private val _state = MutableStateFlow(PrintInvoiceUiState(saleId = saleId))
    val state: StateFlow<PrintInvoiceUiState> = _state.asStateFlow()

    init {
        loadInvoice()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadInvoice() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = salePrintRepo.getSalePrintable(saleId)

            result.fold(
                onSuccess = { invoiceText ->
                    _state.update {
                        it.copy(
                            invoiceText = invoiceText,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = (error as? AppError)?.userFriendlyMessage ?: "Failed to load invoice"
                        )
                    }
                }
            )
        }
    }

    companion object {
        fun provideFactory(saleId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                PrintInvoiceViewModel(
                    salePrintRepo = application.container.salePrintRepo,
                    saleId = saleId
                )
            }
        }
    }
}
