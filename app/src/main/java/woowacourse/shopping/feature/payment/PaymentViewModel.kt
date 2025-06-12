package woowacourse.shopping.feature.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.coupon.CouponRepository
import woowacourse.shopping.data.remote.order.OrderRepository
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Price.Companion.DEFAULT_DISCOUNT
import woowacourse.shopping.domain.model.Price.Companion.DEFAULT_PRICE
import woowacourse.shopping.domain.model.Price.Companion.DEFAULT_SHIPPING_FEE
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import woowacourse.shopping.util.toDomain

class PaymentViewModel(
    private val couponRepository: CouponRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> = _coupons

    private val _price = MutableLiveData<Price>(DEFAULT_PRICE)
    val price: LiveData<Price> = _price

    private val _orderCompletedEvent = MutableSingleLiveData<Unit>()
    val orderCompletedEvent: SingleLiveData<Unit> = _orderCompletedEvent

    private var _orderedCarts: List<CartProduct> = emptyList()
    val orderedCarts: List<CartProduct> get() = _orderedCarts

    private var currentCouponRule: Coupon? = null

    fun setOrderDetails(orderIds: LongArray) {
        viewModelScope
            .launch {
                val allCarts = cartRepository.fetchAllCart().content
                _orderedCarts = allCarts.filter { it.id in orderIds }.map { it.toDomain() }

                val orderedPrice = _orderedCarts.sumOf { it.product.price * it.quantity }

                val newPrice =
                    _price.value?.copy(orderPrice = orderedPrice, totalPrice = orderedPrice + DEFAULT_SHIPPING_FEE)
                        ?: Price(orderPrice = orderedPrice, totalPrice = orderedPrice + DEFAULT_SHIPPING_FEE)
                _price.postValue(newPrice)

                getAvailableCoupons()
            }
    }

    fun toggleCheck(selectedCouponRule: Coupon) {
        val updated =
            _coupons.value?.map {
                if (it.couponDetail.id == selectedCouponRule.couponDetail.id) {
                    it.copyWithApplied(true)
                } else {
                    it.copyWithApplied(false)
                }
            }
        _coupons.value = updated ?: emptyList()

        applyCoupon(selectedCouponRule)
    }

    fun applyCoupon(selectedCouponRule: Coupon) {
        val currentPrice = _price.value ?: Price()

        if (currentCouponRule?.couponDetail?.code == selectedCouponRule.couponDetail.code) {
            _price.value =
                currentPrice.copy(
                    discountPrice = DEFAULT_DISCOUNT,
                    shippingFee = DEFAULT_SHIPPING_FEE,
                    totalPrice = currentPrice.orderPrice + DEFAULT_SHIPPING_FEE,
                )
            currentCouponRule = null
            return
        }

        val contract = Coupon.getContract(selectedCouponRule.couponDetail)
        val newPrice = contract.apply(currentPrice, selectedCouponRule, orderedCarts)

        _price.value = newPrice
        currentCouponRule = selectedCouponRule
    }

    fun order() {
        viewModelScope.launch {
            orderRepository
                .order(_orderedCarts.map { it.id })
                .onSuccess {
                    _orderCompletedEvent.setValue(Unit)
                }
        }
    }

    private fun getAvailableCoupons() {
        viewModelScope.launch {
            val allCoupons = couponRepository.fetchAllCoupons().map { it.toDomain() }
            val orderedPrice = _price.value?.orderPrice ?: 0

            val availableCouponRules =
                allCoupons
                    .filter { couponDetail ->
                        val contract = Coupon.getContract(couponDetail)
                        contract.isAvailable(orderedPrice, orderedCarts)
                    }.map { couponDetail ->
                        Coupon.getContract(couponDetail)
                    }

            _coupons.postValue(availableCouponRules)
        }
    }
}
