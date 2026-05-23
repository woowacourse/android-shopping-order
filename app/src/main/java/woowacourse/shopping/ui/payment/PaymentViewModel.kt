package woowacourse.shopping.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.data.remote.server.repository.CouponRepository
import woowacourse.shopping.data.remote.server.repository.OrderRepository
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.Discount
import woowacourse.shopping.ui.ViewModelConst
import java.time.LocalDateTime

class PaymentViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
    private val checkedItemIds: List<Long>
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<PaymentEvent>()
    val event = _event.asSharedFlow()

    init {
        fetchInitialData()
    }

    private fun fetchInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val cartResult = cartRepository.getPagedCart(0, ViewModelConst.CART_MAX_COUNT)
            val couponResult = couponRepository.getCoupons()

            if (cartResult is ApiResult.Success && couponResult is ApiResult.Success) {
                val checkedProducts = cartResult.data.purchaseProducts.filter { it.id in checkedItemIds }
                val order = Order(
                    purchaseProducts = checkedProducts,
                    currentTime = LocalDateTime.now(),
                    isRemoteArea = false
                )
                _uiState.update {
                    it.copy(
                        order = order,
                        coupons = couponResult.data,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                val errorMessage = when {
                    cartResult is ApiResult.Error -> "${ViewModelConst.NETWORK_ERROR_LABEL}${cartResult.code}"
                    cartResult is ApiResult.Exception -> "${ViewModelConst.ERROR_LABEL}${cartResult.e.message}"
                    couponResult is ApiResult.Error -> "${ViewModelConst.NETWORK_ERROR_LABEL}${couponResult.code}"
                    couponResult is ApiResult.Exception -> "${ViewModelConst.ERROR_LABEL}${couponResult.e.message}"
                    else -> "Unknown Error"
                }
                _event.emit(PaymentEvent.SnackbarEvent(errorMessage))
            }
        }
    }

    fun selectCoupon(coupon: Coupon?) {
        if (coupon != _uiState.value.selectedCoupon) {
            _uiState.update { state ->
                val discount = coupon?.calculateDiscount(state.order) ?: Discount()
                state.copy(
                    selectedCoupon = coupon,
                    discount = discount
                )
            }
        }
    }

    fun processOrder() {
        viewModelScope.launch {
            when (val result = orderRepository.order(uiState.value.order)) {
                is ApiResult.Success -> {
                    _event.emit(
                        PaymentEvent.SnackbarEvent("주문이 완료되었습니다.")
                    )
                    _event.emit(
                        PaymentEvent.NavigateToShopping
                    )
                }
                is ApiResult.Error -> _event.emit(
                    PaymentEvent.SnackbarEvent("${ViewModelConst.NETWORK_ERROR_LABEL}${result.code}")
                )
                is ApiResult.Exception -> _event.emit(
                    PaymentEvent.SnackbarEvent("${ViewModelConst.ERROR_LABEL}${result.e.message}")
                )
            }

        }
    }

    fun orderTrigger() {
        viewModelScope.launch {
            _event.emit(PaymentEvent.Order)
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _event.emit(PaymentEvent.NavigateBack)
        }
    }
}

class PaymentViewModelFactory(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
    private val checkedItemIds: List<Long>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewModel(
                cartRepository = cartRepository,
                orderRepository = orderRepository,
                couponRepository = couponRepository,
                checkedItemIds = checkedItemIds,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
