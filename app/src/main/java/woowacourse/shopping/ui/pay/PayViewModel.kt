package woowacourse.shopping.ui.pay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.coupon.CouponRepository
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Coupon
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.PaymentPrice
import java.io.IOException

class PayViewModel(
    private val couponRepository: CouponRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PayUiState(isLoading = true))
    val uiState: StateFlow<PayUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<PayEvent>()
    val event: SharedFlow<PayEvent> = _event.asSharedFlow()

    private var coupons: List<Coupon> = emptyList()
    private var selectedCartItems: List<CartItem> = emptyList()

    init {
        observePayState()
        refreshCoupons()
    }

    private fun observePayState() {
        viewModelScope.launch {
            combine(
                couponRepository.coupons,
                cartRepository.cartItems,
                cartRepository.selectedCartItemIds,
            ) { coupons, cartItems, selectedCartItemIds ->
                val selectedCartItems = cartItems.filter { cartItem -> cartItem.id in selectedCartItemIds }

                coupons to selectedCartItems.toImmutableList()
            }.collect { (coupons, selectedCartItems) ->
                this@PayViewModel.coupons = coupons
                this@PayViewModel.selectedCartItems = selectedCartItems
                renderPayState()
            }
        }
    }

    private fun refreshCoupons() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                couponRepository.refreshCoupons()
            } catch (_: IOException) {
                _uiState.update { it.copy(errorMessage = "쿠폰을 불러오지 못했습니다.") }
            } catch (_: HttpException) {
                _uiState.update { it.copy(errorMessage = "쿠폰을 불러오지 못했습니다.") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectCoupon(couponId: String) {
        val coupon = coupons.firstOrNull { coupon -> coupon.id == couponId } ?: return
        val calculationResult = coupon.calculate(selectedCartItems)

        if (!calculationResult.isApplicable) return

        _uiState.update { state ->
            state.copy(
                selectedCouponId =
                    if (state.selectedCouponId == couponId) {
                        null
                    } else {
                        couponId
                    },
            )
        }
        renderPayState()
    }

    fun completePay() {
        viewModelScope.launch {
            runCatching {
                cartRepository.deleteSelectedItems()
            }.onSuccess {
                _event.emit(PayEvent.NavigateToShopping)
            }.onFailure { throwable ->
                if (throwable is IOException || throwable is HttpException) {
                    _event.emit(PayEvent.CompletePayFailure)
                } else {
                    throw throwable
                }
            }
        }
    }

    private fun renderPayState() {
        _uiState.update { state ->
            val selectedCoupon = coupons.firstOrNull { coupon -> coupon.id == state.selectedCouponId }
            val selectedCouponCalculationResult = selectedCoupon?.calculate(selectedCartItems)
            val productTotalPrice = selectedCartItems.totalPrice()
            val discountAmount = selectedCouponCalculationResult?.discountAmount ?: Money(0)
            val paymentPrice =
                PaymentPrice(
                    productTotalPrice = productTotalPrice,
                    discountAmount = discountAmount,
                    isFreeShipping = selectedCouponCalculationResult?.isFreeShipping ?: false,
                )
            val selectedCouponId =
                if (selectedCouponCalculationResult?.isApplicable == false) {
                    null
                } else {
                    state.selectedCouponId
                }

            state.copy(
                selectedCouponId = selectedCouponId,
                coupons =
                    coupons
                        .map { coupon ->
                            coupon.toUiModel(
                                selectedCouponId = selectedCouponId,
                                cartItems = selectedCartItems,
                            )
                        }.toImmutableList(),
                totalOrderPrice = productTotalPrice.amount,
                discountAmount = discountAmount.amount,
                shippingFee = paymentPrice.shippingFee.amount,
                finalPrice = paymentPrice.finalPrice.amount,
            )
        }
    }

    companion object {
        fun provideFactory(
            couponRepository: CouponRepository,
            cartRepository: CartRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PayViewModel(
                        couponRepository = couponRepository,
                        cartRepository = cartRepository,
                    )
                }
            }
    }
}

private fun List<CartItem>.totalPrice(): Money = fold(Money(0)) { acc, cartItem -> acc + cartItem.getTotalPrice() }
