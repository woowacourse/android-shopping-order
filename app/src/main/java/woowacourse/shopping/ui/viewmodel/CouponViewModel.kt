package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.backend.retrofit.repository.CouponRetrofitRepository
import woowacourse.shopping.ui.state.CouponUiState

class CouponViewModel(
    private val couponRetrofitRepository: CouponRetrofitRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CouponUiState())
    val uiState: StateFlow<CouponUiState> = _uiState.asStateFlow()

    fun loadCoupons() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            runCatching {
                couponRetrofitRepository.getCoupons()
            }.onSuccess { coupons ->
                _uiState.update {
                    it.copy(
                        coupons = coupons,
                        isLoading = false,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "쿠폰 목록 조회 실패",
                    )
                }
            }
        }
    }

    fun selectCoupon(couponId: Long) {
        _uiState.update { it.copy(selectedCouponId = couponId) }
    }
}