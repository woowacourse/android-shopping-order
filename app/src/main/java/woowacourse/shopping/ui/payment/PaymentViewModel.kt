package woowacourse.shopping.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.CouponRepository
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Payment
import woowacourse.shopping.ui.model.UiPaymentPrice

class PaymentViewModel(
    private val selectedCartItemIds: List<String>,
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<PaymentUiEvent>()
    val uiEvent: SharedFlow<PaymentUiEvent> = _uiEvent.asSharedFlow()

    init {
        loadPayment()
    }

    private fun loadPayment() {
        viewModelScope.launch {
            runCatching {
                _uiState.update { it.copy(isLoading = true) }

                val cartItems = cartRepository.getCartItems(selectedCartItemIds)
                val coupons = couponRepository.getCoupons()

                _uiState.update { state ->
                    val availableCoupons = Payment(cartItems = cartItems).availableCoupons(coupons)
                    val selectedCouponId =
                        if (availableCoupons.any { it.id == state.selectedCouponId }) {
                            state.selectedCouponId
                        } else {
                            null
                        }
                    val selectedCoupon = availableCoupons.firstOrNull { it.id == selectedCouponId }
                    val payment =
                        Payment(
                            cartItems = cartItems,
                            selectedCoupon = selectedCoupon,
                        )

                    state.copy(
                        cartItems = cartItems,
                        coupons = coupons,
                        totalPrice = calculateTotalPrice(cartItems),
                        selectedCouponId = selectedCouponId,
                        paymentPrice = payment.toUiModel(),
                    )
                }
            }.onFailure {
                _uiEvent.emit(
                    PaymentUiEvent.ShowToastMessage("결제 정보를 불러오지 못했습니다."),
                )
            }.also {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun checkCoupon(checkedCouponId: Long) {
        viewModelScope.launch {
            _uiState.update { state ->
                if (state.uiCoupons.none { it.id == checkedCouponId }) return@update state

                val selectedCouponId =
                    if (state.selectedCouponId == checkedCouponId) {
                        null
                    } else {
                        checkedCouponId
                    }

                val selectedCoupon =
                    Payment(cartItems = state.cartItems)
                        .availableCoupons(state.coupons)
                        .firstOrNull { it.id == selectedCouponId }
                val payment =
                    Payment(
                        cartItems = state.cartItems,
                        selectedCoupon = selectedCoupon,
                    )

                state.copy(
                    selectedCouponId = selectedCouponId,
                    paymentPrice = payment.toUiModel(),
                )
            }
        }
    }

    fun payment() {
        viewModelScope.launch {
            _uiEvent.emit(PaymentUiEvent.PaymentSuccess)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _uiEvent.emit(PaymentUiEvent.NavToBack)
        }
    }

    private fun calculateTotalPrice(cartItems: List<CartItem>): Money =
        cartItems.fold(Money(0)) { acc, cartItem ->
            acc + cartItem.getTotalPrice()
        }

    private fun Payment.toUiModel(): UiPaymentPrice =
        UiPaymentPrice(
            totalPrice = totalPrice.amount,
            deliveryFee = deliveryFee.amount,
            couponDiscountPrice = -discountPrice.amount,
            paymentPrice = finalPrice.amount,
        )

    companion object {
        fun Factory(selectedCartItemIds: List<String>): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication

                    PaymentViewModel(
                        selectedCartItemIds = selectedCartItemIds,
                        cartRepository = app.appContainer.cartRepository,
                        couponRepository = app.appContainer.couponRepository,
                    )
                }
            }
    }
}
