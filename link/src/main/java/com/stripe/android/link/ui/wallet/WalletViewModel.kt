package com.stripe.android.link.ui.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stripe.android.core.Logger
import com.stripe.android.link.LinkActivityResult
import com.stripe.android.link.LinkConfiguration
import com.stripe.android.link.LinkScreen
import com.stripe.android.link.account.LinkAccountManager
import com.stripe.android.link.injection.NativeLinkComponent
import com.stripe.android.link.model.LinkAccount
import com.stripe.android.link.model.supportedPaymentMethodTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class WalletViewModel @Inject constructor(
    private val configuration: LinkConfiguration,
    private val linkAccount: LinkAccount,
    private val linkAccountManager: LinkAccountManager,
    private val logger: Logger,
    private val navigate: (route: LinkScreen, clearStack: Boolean) -> Unit,
    private val dismissWithResult: (LinkActivityResult) -> Unit
) : ViewModel() {
    private val stripeIntent = configuration.stripeIntent

    private val _uiState = MutableStateFlow(
        value = WalletUiState(
            supportedTypes = stripeIntent.supportedPaymentMethodTypes(linkAccount),
            paymentDetailsList = emptyList(),
            selectedItem = null,
            isProcessing = false
        )
    )

    val uiState: StateFlow<WalletUiState> = _uiState

    init {
        loadPaymentDetails()
    }

    private fun loadPaymentDetails() {
        _uiState.update {
            it.setProcessing()
        }

        viewModelScope.launch {
            linkAccountManager.listPaymentDetails(
                paymentMethodTypes = _uiState.value.supportedTypes
            ).fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.updateWithResponse(response)
                    }

                    if (response.paymentDetails.isEmpty()) {
                        navigate(LinkScreen.PaymentMethod, true)
                    }
                },
                // If we can't load the payment details there's nothing to see here
                onFailure = ::onFatal
            )
        }
    }

    private fun onFatal(fatalError: Throwable) {
        logger.error("WalletViewModel Fatal error: ", fatalError)
        dismissWithResult(LinkActivityResult.Failed(fatalError))
    }

    companion object {
        fun factory(
            parentComponent: NativeLinkComponent,
            linkAccount: LinkAccount,
            navigate: (route: LinkScreen, clearStack: Boolean) -> Unit,
            dismissWithResult: (LinkActivityResult) -> Unit
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    WalletViewModel(
                        configuration = parentComponent.configuration,
                        linkAccountManager = parentComponent.linkAccountManager,
                        logger = parentComponent.logger,
                        linkAccount = linkAccount,
                        navigate = navigate,
                        dismissWithResult = dismissWithResult
                    )
                }
            }
        }
    }
}