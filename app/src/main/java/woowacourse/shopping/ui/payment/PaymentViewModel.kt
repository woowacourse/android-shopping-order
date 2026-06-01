package woowacourse.shopping.ui.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.cart.CartItems
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponCalculator
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.ui.navigation.PaymentRoute

class PaymentViewModel(
    savedStateHandle: SavedStateHandle,
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val route: PaymentRoute = savedStateHandle.toRoute<PaymentRoute>()
    private val selectedItemIds: Set<Int> = route.selectedItemIds.toSet()
    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<PaymentUiEvent>()
    val uiEvent: SharedFlow<PaymentUiEvent> = _uiEvent.asSharedFlow()

    private var availableCoupons: List<Coupon> = emptyList()

    init {
        refresh()
        viewModelScope.launch {
            cartRepository.cartEvents.collect {
                refresh()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val previousSelectedCoupon = (_uiState.value as? PaymentUiState.Success)?.selectedCoupon
            _uiState.value = PaymentUiState.Loading
            val allCart = cartRepository.getAllCartItems()

            val selectedCart =
                if (selectedItemIds.isEmpty()) {
                    allCart
                } else {
                    CartItems(allCart.values.filter { it.id in selectedItemIds })
                }

            availableCoupons = couponRepository.getAvailableCoupons()
            val selectedCoupon =
                previousSelectedCoupon?.let { previous ->
                    availableCoupons.firstOrNull { it.code == previous.code }
                } ?: availableCoupons.firstOrNull()
            _uiState.value =
                buildSuccessState(
                    cartItems = selectedCart,
                    availableCoupons = availableCoupons,
                    selectedCoupon = selectedCoupon,
                )
        }
    }

    fun selectCoupon(coupon: Coupon?) {
        val current = _uiState.value as? PaymentUiState.Success ?: return
        val newState =
            buildSuccessState(
                cartItems = current.cartItems,
                availableCoupons = current.availableCoupons,
                selectedCoupon = coupon,
            )
        _uiState.value = newState
    }

    fun onClickPay() {
        viewModelScope.launch {
            val current = _uiState.value as? PaymentUiState.Success ?: return@launch
            val cartItems = current.cartItems
            val selectedIds = cartItems.values.map { it.id }
            if (selectedIds.isEmpty()) {
                _uiEvent.emit(PaymentUiEvent.ShowMessage("선택된 상품이 없습니다."))
            }
            cartRepository.order(selectedIds)
            _uiEvent.emit(PaymentUiEvent.ShowMessage("주문이 완료되었습니다"))
            _uiEvent.emit(PaymentUiEvent.OrderSucceeded)
        }
    }

    private fun buildSuccessState(
        cartItems: CartItems,
        availableCoupons: List<Coupon>,
        selectedCoupon: Coupon?,
    ): PaymentUiState.Success {
        val applyResult = CouponCalculator.apply(selectedCoupon, cartItems)
        val subtotal = cartItems.totalPrice
        val couponDiscount = applyResult.discount
        val shippingFee = applyResult.shippingFee
        val totalPrice = subtotal - couponDiscount + shippingFee

        return PaymentUiState.Success(
            cartItems = cartItems,
            availableCoupons = availableCoupons,
            selectedCoupon = selectedCoupon,
            subtotal = subtotal,
            couponDiscount = couponDiscount,
            shippingFee = shippingFee,
            totalPrice = totalPrice,
        )
    }

    companion object {
        fun factory(
            cartRepository: CartRepository,
            couponRepository: CouponRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PaymentViewModel(
                        savedStateHandle = createSavedStateHandle(),
                        cartRepository = cartRepository,
                        couponRepository = couponRepository,
                    )
                }
            }
    }
}
