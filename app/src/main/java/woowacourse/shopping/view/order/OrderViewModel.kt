package woowacourse.shopping.view.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.order.adapter.OnClickCoupon
import java.time.LocalDate
import java.time.LocalTime

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel(), OnClickCoupon {
    val shoppingCart = ShoppingCart()

    private val _coupons: MutableLiveData<List<Coupon>> = MutableLiveData()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            runCatching {
                orderRepository.getCoupons().getOrThrow()
            }.onSuccess { couponList ->
                _coupons.value = couponList
            }.onFailure {
                Log.e("OrderViewModel", "loadCoupons: error $it")
            }
        }
    }

    fun applyCoupon(coupon: Coupon) {
        when (coupon) {
            is Coupon.FixedDiscountCoupon -> applyFixedDiscountCoupon(coupon)
            is Coupon.BogoCoupon -> applyBogoCoupon(coupon)
            is Coupon.FreeShippingCoupon -> applyFreeShippingCoupon(coupon)
            is Coupon.TimeBasedDiscountCoupon -> applyTimeBasedDiscountCoupon(coupon)
        }
    }

    private fun applyFixedDiscountCoupon(coupon: Coupon.FixedDiscountCoupon) {
        if (totalPrice.value!! >= coupon.minimumAmount &&
            LocalDate.now()
                .isBefore(coupon.expirationDate)
        ) {
            _totalPrice.value = _totalPrice.value!! - coupon.discount
        }
    }

    private fun applyBogoCoupon(coupon: Coupon.BogoCoupon) {
        val cartItems = shoppingCart.cartItems.value.orEmpty()
        val eligibleItems =
            cartItems.filter { it.product.cartItemCounter.itemCount >= coupon.buyQuantity }
        val mostExpensiveItem = eligibleItems.maxByOrNull { it.product.price }

        if (mostExpensiveItem != null && LocalDate.now().isBefore(coupon.expirationDate)) {
            val discountAmount = mostExpensiveItem.product.price
            _totalPrice.value = _totalPrice.value!! - discountAmount
        }
    }

    private fun applyFreeShippingCoupon(coupon: Coupon.FreeShippingCoupon) {
        if (totalPrice.value!! >= coupon.minimumAmount &&
            LocalDate.now()
                .isBefore(coupon.expirationDate)
        ) {
            _totalPrice.value = _totalPrice.value!! - SHIPPING_COST
        }
    }

    private fun applyTimeBasedDiscountCoupon(coupon: Coupon.TimeBasedDiscountCoupon) {
        val now = LocalTime.now()
        if (now.isAfter(coupon.availableTimeStart) &&
            now.isBefore(coupon.availableTimeEnd) &&
            LocalDate.now().isBefore(coupon.expirationDate)
        ) {
            _totalPrice.value = (_totalPrice.value!! * (1 - coupon.discount / 100.0)).toInt()
        }
    }

    companion object {
        const val SHIPPING_COST = 3000
    }

    override fun clickCoupon() {
        TODO("Not yet implemented")
    }
}
