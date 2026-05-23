package woowacourse.shopping.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.repository.CouponRepository
import woowacourse.shopping.repository.ShoppingRepositoryProvider
import woowacourse.shopping.ui.payment.uistate.CouponUiModelMapper
import woowacourse.shopping.ui.payment.uistate.PaymentUiState
import java.time.LocalDate
import java.time.LocalTime

class PaymentViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            couponRepository
                .getCoupons()
                .onSuccess { fetchedCoupons ->
                    val currentDate = LocalDate.now()
                    val currentTime = LocalTime.now()
                    val couponUiModels =
                        fetchedCoupons
                            .filter { it.isApplicable(currentDate, currentTime) }
                            .map { CouponUiModelMapper.toUiModel(it) }
                    _uiState.update {
                        it.copy(
                            coupons = couponUiModels,
                            isLoading = false,
                        )
                    }
                }.onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message) }
                }
        }
    }

    fun selectCoupon(couponId: Long) {
        _uiState.update { currentState ->
            val updatedCoupons =
                currentState.coupons.map { uiModel ->
                    if (uiModel.id == couponId) {
                        uiModel.copy(isSelected = !uiModel.isSelected)
                    } else {
                        uiModel.copy(isSelected = false)
                    }
                }
            currentState.copy(coupons = updatedCoupons)
        }
    }
}

class PaymentViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        PaymentViewModel(
            couponRepository = ShoppingRepositoryProvider.couponRepository,
        ) as T
}
