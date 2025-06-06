package woowacourse.shopping.feature.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.coupon.CouponRepository
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.util.toDomain
import java.time.LocalTime

class PaymentViewModel(
    private val couponRepository: CouponRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> = _coupons

    private val _price = MutableLiveData<Price>(Price())
    val price: LiveData<Price> = _price

    private var orderedCarts: List<CartProduct> = emptyList()

    fun setOrderDetails(orderIds: LongArray) {
        viewModelScope
            .launch {
                val allCarts = cartRepository.fetchAllCart().content
                orderedCarts = allCarts.filter { it.id in orderIds }.map { it.toDomain() }
                val orderedPrice = orderedCarts.sumOf { it.product.price * it.quantity }

                val newPrice =
                    _price.value?.copy(orderPrice = orderedPrice, totalPrice = orderedPrice)
                        ?: Price(orderPrice = orderedPrice, totalPrice = orderedPrice)
                _price.postValue(newPrice)

                getAvailableCoupons()
            }
    }

    fun toggleCheck(selectedCoupon: Coupon) {
        val updated =
            _coupons.value?.map {
                if (it.couponDetail.id == selectedCoupon.couponDetail.id) {
                    it.copy(isChecked = true)
                } else {
                    it.copy(isChecked = false)
                }
            }
        _coupons.value = updated ?: emptyList()
    }

    private fun getAvailableCoupons() {
        viewModelScope.launch {
            val allCoupons = couponRepository.fetchAllCoupons().map { it.toDomain() }
            val orderedPrice = _price.value?.orderPrice ?: 0

            val result = mutableListOf<Coupon>()

            if (orderedPrice > 50_000) {
                allCoupons.find { it.code == "FREESHIPPING" }?.let { result.add(Coupon(couponDetail = it)) }
            }

            if (orderedPrice > 100_000) {
                allCoupons.find { it.code == "FIXED5000" }?.let { result.add(Coupon(couponDetail = it)) }
            }

            val hasBulkItem = orderedCarts.any { it.quantity >= 3 }
            if (hasBulkItem) {
                allCoupons.find { it.code == "BOGO" }?.let { result.add(Coupon(couponDetail = it)) }
            }

            val isDawnTime =
                LocalTime.now().isAfter(LocalTime.of(4, 0)) &&
                    LocalTime.now().isBefore(LocalTime.of(7, 0))
            if (isDawnTime) {
                allCoupons.find { it.code == "MIRACLESALE" }?.let { result.add(Coupon(couponDetail = it)) }
            }

            _coupons.postValue(result)
        }
    }
}
