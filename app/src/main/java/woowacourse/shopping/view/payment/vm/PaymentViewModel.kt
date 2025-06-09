package woowacourse.shopping.view.payment.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.coupon.AvailableCoupons
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.payment.state.CouponUi
import woowacourse.shopping.view.payment.state.PaymentUi
import woowacourse.shopping.view.payment.state.PaymentUiState
import woowacourse.shopping.view.payment.state.toCouponUi
import java.time.LocalDateTime

class PaymentViewModel(
    private val couponRepository: CouponRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _paymentUiState = MutableLiveData<PaymentUiState>()
    val paymentUiState: LiveData<PaymentUiState> get() = _paymentUiState

    private var coupons: List<Coupon> = emptyList()

    private var couponPairs: List<Pair<Coupon, CouponUi>> = emptyList()

    private val shippingPrice = 3_000

    private var currentOrderItems: List<ShoppingCart> = emptyList()

    private val _isCompletedOrder = MutableSingleLiveData<Boolean>()
    val isCompletedOrder: SingleLiveData<Boolean> get() = _isCompletedOrder

    fun setCartItems(orderItems: List<ShoppingCart>) {
        currentOrderItems = orderItems
        loadAvailableCoupons(orderItems)
    }

    fun loadAvailableCoupons(orderItems: List<ShoppingCart>) {
        viewModelScope.launch(Dispatchers.IO) {
            coupons = fetchAvailableCoupons(orderItems)
            couponPairs = convertToCouponUiPairs(coupons)

            val paymentUiInfo = buildPaymentUiState(couponPairs, orderItems)
            _paymentUiState.postValue(paymentUiInfo)
        }
    }

    private suspend fun fetchAvailableCoupons(orderItems: List<ShoppingCart>): List<Coupon> {
        val coupons = couponRepository.getAll()
        return AvailableCoupons(coupons, orderItems, LocalDateTime.now()).get()
    }

    private fun convertToCouponUiPairs(coupons: List<Coupon>): List<Pair<Coupon, CouponUi>> {
        return coupons.map { it to it.toCouponUi() }
    }

    private fun buildPaymentUiState(
        couponPairs: List<Pair<Coupon, CouponUi>>,
        orderItems: List<ShoppingCart>,
    ): PaymentUiState {
        val totalPrice = orderItems.sumOf { it.product.priceValue * it.quantity.value }
        val selectedCoupon = couponPairs.find { it.second.checked }?.first
        val discountPrice = selectedCoupon?.calculateDiscount(orderItems) ?: 0

        return PaymentUiState(
            coupons = couponPairs.map { it.second },
            paymentUi =
                PaymentUi(
                    orderPrice = totalPrice,
                    discountPrice = -discountPrice,
                    shippingPrice = shippingPrice,
                    totalPrice = totalPrice + shippingPrice - discountPrice,
                ),
        )
    }

    fun toggleCouponChecked(
        couponUi: CouponUi,
        isChecked: Boolean,
    ) {
        val currentState = _paymentUiState.value ?: return
        val updatedCouponUis =
            currentState.coupons.map {
                if (it.title == couponUi.title) it.copy(checked = isChecked) else it
            }

        val orderItems = currentOrderItems
        if (orderItems.isEmpty()) return

        val selectedCoupon = couponPairs.find { it.second.title == couponUi.title }?.first
        val discountPrice = selectedCoupon?.calculateDiscount(orderItems) ?: 0

        val totalPrice = orderItems.sumOf { it.product.priceValue * it.quantity.value }

        val newState =
            PaymentUiState(
                coupons = updatedCouponUis,
                paymentUi =
                    PaymentUi(
                        orderPrice = totalPrice,
                        discountPrice = -discountPrice,
                        shippingPrice = shippingPrice,
                        totalPrice = totalPrice + shippingPrice - discountPrice,
                    ),
            )

        _paymentUiState.value = newState
    }

    fun onClickOrder() {
        val ids = currentOrderItems.map { it.id }
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.createOrder(ids)
            _isCompletedOrder.postValue(true)
        }
    }
}
