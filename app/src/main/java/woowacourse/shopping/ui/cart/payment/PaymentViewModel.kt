package woowacourse.shopping.ui.cart.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.CouponRepository
import woowacourse.shopping.model.CouponOrderItem
import woowacourse.shopping.ui.cart.SelectedCartItem

class PaymentViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState(deliveryFee = DEFAULT_DELIVERY_FEE))
    val uiState = _uiState.asStateFlow()

    init {
        loadCoupons()
    }

    fun selectCoupon(couponId: Long) {
        _uiState.update { state ->
            state
                .copy(
                    selectedCouponId =
                        if (state.selectedCouponId == couponId) {
                            null
                        } else {
                            couponId
                        },
                ).calculatePaymentPrice()
        }
    }

    fun updateOrder(
        orderPrice: Long,
        selectedCartItems: Map<Long, SelectedCartItem>,
    ) {
        val orderItems =
            selectedCartItems.map { selectedCartItem ->
                CouponOrderItem(
                    totalPrice = selectedCartItem.value.totalPrice,
                    quantity = selectedCartItem.value.quantity,
                )
            }

        _uiState.update { state ->
            state
                .copy(
                    orderPrice = orderPrice,
                    orderItems = orderItems,
                    totalQuantity = orderItems.sumOf { it.quantity },
                ).calculatePaymentPrice()
        }
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            couponRepository
                .getCoupons()
                .onSuccess { coupons ->
                    val couponModels = coupons.toImmutableList()
                    _uiState.update { state ->
                        state
                            .copy(
                                coupons = couponModels,
                                selectedCouponId = couponModels.firstOrNull()?.id,
                                isLoading = false,
                                errorMessage = null,
                            ).calculatePaymentPrice()
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "쿠폰 정보를 불러오지 못했습니다.",
                        )
                    }
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val appContainer = (this[APPLICATION_KEY] as ShoppingApplication).appContainer

                    PaymentViewModel(
                        couponRepository = appContainer.couponRepository,
                    )
                }
            }

        private const val DEFAULT_DELIVERY_FEE = 3000L
    }
}

private fun PaymentUiState.calculatePaymentPrice(): PaymentUiState {
    val selectedCoupon = coupons.firstOrNull { it.id == selectedCouponId }
    val couponDiscountPrice =
        selectedCoupon?.calculateDiscountPrice(
            orderPrice = orderPrice,
            deliveryFee = deliveryFee,
            orderItems = orderItems,
        ) ?: 0L
    val totalPaymentPrice = (orderPrice - couponDiscountPrice + deliveryFee).coerceAtLeast(0)

    return copy(
        couponDiscountPrice = couponDiscountPrice,
        totalPaymentPrice = totalPaymentPrice,
    )
}
