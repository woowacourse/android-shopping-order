package woowacourse.shopping.view.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.utils.exception.ErrorEvent
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.BaseViewModel
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.recommend.RecommendEvent

class PaymentViewModel(
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : BaseViewModel(), OnclickPayment {
    private val _coupons: MutableLiveData<List<Coupon>> = MutableLiveData(emptyList())
    val coupons: LiveData<List<Coupon>> get() = _coupons

    private val _totalDiscountPrice: MutableLiveData<Int> = MutableLiveData(DEFAULT_PRICE)
    val totalDiscountPrice: LiveData<Int> get() = _totalDiscountPrice

    private var shoppingCart = ShoppingCart()
    val deliveryCharge = couponRepository.loadDeliveryCharge()

    private val _totalOrderPrice: MutableLiveData<Int> = MutableLiveData(DEFAULT_PRICE)
    val totalOrderPrice: LiveData<Int> get() = _totalDiscountPrice

    private val _paymentEvent: MutableSingleLiveData<PaymentEvent> = MutableSingleLiveData()
    val paymentEvent : SingleLiveData<PaymentEvent> get() = _paymentEvent

    override fun clickCoupon() {

    }

    override fun clickPayment() {
        orderItems()
    }

    private fun orderItems() =
        viewModelScope.launch {
            val ids = shoppingCart.cartItems.value?.map { it.id.toInt() }
            orderRepository.orderShoppingCart(ids ?: throw ErrorEvent.OrderItemsEvent())
                .onSuccess {
                    _paymentEvent.setValue(PaymentEvent.Order.Success)
                }
                .onFailure {
                    handleException(ErrorEvent.OrderItemsEvent())
                }
        }

    fun loadCoupons() = viewModelScope.launch {
        couponRepository.loadCoupons()
            .onSuccess {
                _coupons.value = it
            }
            .onFailure {
                handleException(ErrorEvent.LoadDataEvent())
            }
    }

    fun saveCheckedShoppingCarts(shoppingCart: ShoppingCart) {
        this.shoppingCart = shoppingCart
        _totalOrderPrice.value = shoppingCart.getTotalPrice()
    }

    companion object {
        private const val DEFAULT_PRICE = 0
    }

}
