package woowacourse.shopping.ui.payment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.event.Event
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel
import woowacourse.shopping.ui.payment.action.PaymentNavigationActions
import woowacourse.shopping.ui.payment.action.PaymentNotifyingActions
import woowacourse.shopping.ui.payment.listener.PaymentClickListener
import woowacourse.shopping.ui.state.UiState

class PaymentViewmodel(
    private val cartItemIds: List<Int>,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModel(), PaymentClickListener {
    private val _paymentUiState = MutableLiveData<UiState<List<Coupon>>>(UiState.Loading)
    val paymentUiState: LiveData<UiState<List<Coupon>>>
        get() = _paymentUiState

    private val _coupons = MutableLiveData<List<Coupon>>(emptyList())
    private val coupons: LiveData<List<Coupon>>
        get() = _coupons

    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    private val cartItems: LiveData<List<CartItem>>
        get() = _cartItems

    private val _orderPrice = MutableLiveData<Int>()
    val orderPrice: LiveData<Int>
        get() = _orderPrice

    private val _discountPrice = MutableLiveData<Int>()
    val discountPrice: LiveData<Int>
        get() = _discountPrice

    private val _deliveryPrice = MutableLiveData<Int>()
    val deliveryPrice: LiveData<Int>
        get() = _deliveryPrice

    val totalPrice: LiveData<Int> =
        MediatorLiveData<Int>().apply {

            addSource(discountPrice) { discount ->
                val order = orderPrice.value ?: 0
                val delivery = deliveryPrice.value ?: 0
                value = order - discount + delivery
            }
            addSource(deliveryPrice) { delivery ->
                val order = orderPrice.value ?: 0
                val discount = discountPrice.value ?: 0
                value = order - discount + delivery
            }
        }

    val isCouponEmpty: LiveData<Boolean>
        get() =
            coupons.map { couponsValue ->
                if (paymentUiState.value is UiState.Success) {
                    couponsValue.isEmpty()
                } else {
                    false
                }
            }

    private val _paymentNavigationActions = MutableLiveData<Event<PaymentNavigationActions>>()
    val paymentNavigationActions: LiveData<Event<PaymentNavigationActions>>
        get() = _paymentNavigationActions

    private val _paymentNotifyingActions = MutableLiveData<Event<PaymentNotifyingActions>>()
    val paymentNotifyingActions: LiveData<Event<PaymentNotifyingActions>>
        get() = _paymentNotifyingActions

    init {
        loadCoupons()
        loadCartItems()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            couponRepository.getCoupons().onSuccess { coupons ->
                _paymentUiState.value = UiState.Success(coupons)
                _coupons.value = coupons
            }.onFailure {
                _paymentUiState.value = UiState.Error(it)
            }
        }
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            val totalQuantity = cartRepository.getCartTotalQuantity().getOrNull() ?: 0
            cartRepository.getCartItems(0, totalQuantity, OrderViewModel.DESCENDING_SORT_ORDER)
                .onSuccess {
                    _cartItems.value =
                        it.filter { cartItem -> cartItemIds.contains(cartItem.cartItemId) }
                    _orderPrice.value = cartItems.value?.sumOf { cartItem -> cartItem.totalPrice }
                    _deliveryPrice.value = DEFAULT_DELIVERY_PRICE
                }
        }
    }

    override fun onBackButtonClick() {
        _paymentNavigationActions.value = Event(PaymentNavigationActions.NavigateToBack)
    }

    override fun onCouponClick(couponId: Int) {
        var clickedCoupon = coupons.value?.first { coupon -> coupon.id == couponId } ?: return
        clickedCoupon =
            if (clickedCoupon.isChecked) clickedCoupon.unCheck() else clickedCoupon.check()
        _coupons.value =
            coupons.value?.map { coupon ->
                if (coupon.id != couponId) {
                    coupon.unCheck()
                } else {
                    clickedCoupon
                }
            }

        _paymentUiState.value = UiState.Success(coupons.value ?: emptyList())
        _discountPrice.value = if (clickedCoupon.isChecked) clickedCoupon.discount ?: 0 else 0
    }

    override fun onPaymentButtonClick() {
        viewModelScope.launch {
            orderRepository.postOrder(cartItemIds).onSuccess {
                _paymentNavigationActions.value = Event(PaymentNavigationActions.NavigateToHome)
                _paymentNotifyingActions.value =
                    Event(PaymentNotifyingActions.NotifyPaymentCompleted)
            }
        }
    }

    companion object {
        private const val DEFAULT_DELIVERY_PRICE = 3000
    }
}
