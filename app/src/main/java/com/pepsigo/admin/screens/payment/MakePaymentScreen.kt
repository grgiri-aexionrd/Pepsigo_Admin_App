package com.pepsigo.admin.screens.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun MakePaymentScreen(
    viewModel: MakePaymentViewModel,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    when (state.step) {
        PaymentStep.Entry -> {
            MakePaymentEntryScreen(
                state = state,
                onSelected = viewModel::onUserSelected,
                onCustomerSearchChange = viewModel::onCustomerSearchQueryChange,
                onVendorSearchChange = viewModel::onVendorSearchQueryChange,
                paymentAmountChange = viewModel::onPaymentAmountChange,
                paymentMethodChange = viewModel::onPaymentMethodChange,
                transactionTypeChange = viewModel::onTransactionTypeChange,
                refNumberChange = viewModel::onRefNumberChange,
                validatePayment = viewModel::validatePayment,
                onBack = onNavigateBack,
                onRetry =  viewModel:: retryLoadParties,
                onPartyTypeChanged = viewModel::onPartyTypeChanged,
                onCashChange = viewModel::updateCashDenomination,
                onDigitalChange = viewModel::updateDigitalDenomination
            )
        }

        PaymentStep.Review -> {
            MakePaymentReviewScreen(
                state = state,
                onConfirm = viewModel::createPayment,
                onEdit = viewModel::goBackToEntry
            )
        }

        PaymentStep.Success -> {
            MakePaymentSuccessScreen(
                onDone = onPaymentSuccess
            )
        }
    }
}