package woowacourse.shopping.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.backend.retrofit.repository.CouponRetrofitRepository
import woowacourse.shopping.model.Order
import woowacourse.shopping.ui.state.CouponUiState
import java.time.LocalDateTime

class CouponViewModel(
    private val couponRetrofitRepository: CouponRetrofitRepository,
) : ViewModel() {
    private var currentOrder: Order? = null
    private var orderedCartItemIds: Set<Long> = emptySet()

    private val _uiState = MutableStateFlow(CouponUiState())
    val uiState: StateFlow<CouponUiState> = _uiState.asStateFlow()

    fun initialize(
        order: Order,
        orderedCartItemIds: Set<Long>,
    ) {
        currentOrder = order
        this.orderedCartItemIds = orderedCartItemIds
        recalculate(selectedCouponId = null)
    }

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
                recalculate(selectedCouponId = _uiState.value.selectedCouponId)
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
        val nextCouponId =
            if (_uiState.value.selectedCouponId == couponId) null else couponId
        recalculate(selectedCouponId = nextCouponId)
    }

    fun getOrderedCartItemIds(): Set<Long> = orderedCartItemIds

    private fun recalculate(selectedCouponId: Long?) {
        val order = currentOrder ?: return
        val selectedCoupon =
            _uiState.value.coupons.firstOrNull { it.id == selectedCouponId }

        val orderAmount = order.totalAmount().toInt()
        val shippingFee = 3_000
        val discountAmount =
            selectedCoupon?.discountAmount(order, LocalDateTime.now())?.toInt() ?: 0
        val totalPaymentAmount =
            (orderAmount + shippingFee - discountAmount).coerceAtLeast(0)

        _uiState.update {
            it.copy(
                selectedCouponId = selectedCouponId,
                orderAmount = orderAmount,
                shippingFee = shippingFee,
                discountAmount = discountAmount,
                totalPaymentAmount = totalPaymentAmount,
            )
        }
    }
}
