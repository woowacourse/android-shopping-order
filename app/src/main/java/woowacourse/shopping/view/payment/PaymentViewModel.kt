package woowacourse.shopping.view.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.SelectCouponResult
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.utils.exception.ErrorEvent
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.BaseViewModel
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.payment.model.CouponCalculator
import woowacourse.shopping.view.payment.model.CouponCalculator.Companion.DEFAULT_PRICE

class PaymentViewModel(
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : BaseViewModel(), OnclickPayment {
    private var shoppingCart = ShoppingCart()
    val couponCalculator = CouponCalculator()

    private val _totalOrderPrice: MutableLiveData<Int> = MutableLiveData(DEFAULT_PRICE)
    val totalOrderPrice: LiveData<Int> get() = _totalOrderPrice

    val deliveryCharge = couponRepository.loadDeliveryCharge()

    private val _paymentEvent: MutableSingleLiveData<PaymentEvent> = MutableSingleLiveData()
    val paymentEvent: SingleLiveData<PaymentEvent> get() = _paymentEvent

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

    fun loadCoupons() =
        viewModelScope.launch {
            couponRepository.loadCoupons()
                .onSuccess {
                    couponCalculator.loadCoupons(it)
                }
                .onFailure {
                    handleException(ErrorEvent.LoadDataEvent())
                }
        }

    fun saveCheckedShoppingCarts(shoppingCart: ShoppingCart) {
        this.shoppingCart = shoppingCart
        _totalOrderPrice.value = shoppingCart.getTotalPrice()
    }

    override fun clickCoupon(coupon: Coupon) {
        val selectCouponResult =
            couponCalculator.selectCoupon(
                coupon = coupon,
                shoppingCart = shoppingCart,
                deliveryCharge = deliveryCharge,
            )
        when (selectCouponResult) {
            SelectCouponResult.InValidCount -> _paymentEvent.setValue(PaymentEvent.SelectCoupon.InvalidCount)
            SelectCouponResult.InValidDate -> _paymentEvent.setValue(PaymentEvent.SelectCoupon.InvalidDate)
            SelectCouponResult.InValidPrice -> _paymentEvent.setValue(PaymentEvent.SelectCoupon.InvalidPrice)
            SelectCouponResult.Valid -> _paymentEvent.setValue(PaymentEvent.SelectCoupon.Success)
        }
    }

    override fun clickPayment() {
        orderItems()
    }
}
