package woowacourse.shopping.ui.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.order.Order
import woowacourse.shopping.domain.model.payment.Payment
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.event.UiEvent
import woowacourse.shopping.ui.navigation.ShoppingRoute

class PaymentViewModel internal constructor(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
    val selectedCartItemIds: List<Long>,
) : ViewModel() {
    constructor(
        cartRepository: CartRepository,
        couponRepository: CouponRepository,
        orderRepository: OrderRepository,
        savedStateHandle: SavedStateHandle,
    ) : this(
        cartRepository = cartRepository,
        couponRepository = couponRepository,
        orderRepository = orderRepository,
        selectedCartItemIds =
            savedStateHandle.toRoute<ShoppingRoute.Payment>().selectedCartItemIds,
    )

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<UiEvent> = _uiEvent.receiveAsFlow()

    private val _coupons = MutableStateFlow<List<Coupon>>(emptyList())
    val coupons: StateFlow<List<Coupon>> = _coupons.asStateFlow()

    private val _payment = MutableStateFlow(Payment(selectedCoupon = null))
    val payment: StateFlow<Payment> = _payment.asStateFlow()

    init {
        fetchCoupons()
        fetchOrder()
    }

    fun selectCoupon(couponCode: String) {
        _payment.update { payment ->
            val selectedCoupon =
                if (payment.selectedCoupon?.code == couponCode) {
                    null
                } else {
                    coupons.value
                        .firstOrNull { it.code == couponCode }
                        ?.takeIf { payment.canApply(it) }
                }
            payment.copy(selectedCoupon = selectedCoupon)
        }
    }

    fun completePayment(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                orderRepository.createOrder(selectedCartItemIds)
                _uiEvent.send(UiEvent.ShowMessage("주문이 완료되었습니다."))
                onSuccess()
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowMessage("주문에 실패했습니다. 다시 시도해주세요."))
            }
        }
    }

    private fun fetchCoupons() {
        viewModelScope.launch {
            try {
                val fetchedCoupons = couponRepository.getCoupons()
                _coupons.update { fetchedCoupons }
                _payment.update { payment ->
                    payment.withValidSelectedCoupon(fetchedCoupons)
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowMessage("쿠폰을 불러오지 못했습니다."))
            }
        }
    }

    private fun fetchOrder() {
        viewModelScope.launch {
            try {
                val cartItems = cartRepository.getAllCartItems()
                val order =
                    Order.fromSelectedCartItems(
                        cartItems = cartItems,
                        selectedCartItemIds = selectedCartItemIds,
                    )
                _payment.update { payment ->
                    payment
                        .copy(order = order)
                        .withValidSelectedCoupon(coupons.value)
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowMessage("주문 금액을 불러오지 못했습니다."))
            }
        }
    }

    private fun Payment.withValidSelectedCoupon(coupons: List<Coupon>): Payment {
        val validSelectedCoupon =
            coupons.firstOrNull { coupon ->
                coupon.code == selectedCoupon?.code && canApply(coupon)
            } ?: coupons.firstOrNull { canApply(it) }
        return copy(selectedCoupon = validSelectedCoupon)
    }
}

class PaymentViewModelFactory(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewModel(
                cartRepository = cartRepository,
                couponRepository = couponRepository,
                orderRepository = orderRepository,
                savedStateHandle = extras.createSavedStateHandle(),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
