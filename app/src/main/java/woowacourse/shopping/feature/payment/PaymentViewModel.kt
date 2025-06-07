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
import woowacourse.shopping.domain.model.CouponContract
import woowacourse.shopping.domain.model.DiscountType
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.util.toDomain

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

        applyCoupon()
    }

    fun applyCoupon() {
        val checkedCoupon = _coupons.value?.find { it.isChecked }
        val discountPrice = checkedCoupon?.couponDetail?.discount ?: 0
        val newDiscountedPrice = _price.value?.orderPrice?.minus(discountPrice) ?: 0
        val newPrice =
            _price.value?.copy(discountPrice = discountPrice, totalPrice = newDiscountedPrice)
                ?: Price(discountPrice = discountPrice, totalPrice = newDiscountedPrice)
        _price.value = newPrice
    }

    private fun getAvailableCoupons() {
        viewModelScope.launch {
            val allCoupons = couponRepository.fetchAllCoupons().map { it.toDomain() }
            val orderedPrice = _price.value?.orderPrice ?: 0

            val availableCoupons =
                allCoupons
                    .filter { coupon ->
                        when (coupon.discountType) {
                            DiscountType.FREE_SHIPPING.code -> CouponContract.FreeShippingCoupon.isAvailable(orderedPrice, orderedCarts)
                            DiscountType.FIXED.code -> CouponContract.FixedCoupon.isAvailable(orderedPrice, orderedCarts)
                            DiscountType.BUY_X_GET_Y.code -> CouponContract.BuyTwoGetOneCoupon.isAvailable(orderedPrice, orderedCarts)
                            DiscountType.PERCENTAGE.code -> CouponContract.MiracleSaleCoupon.isAvailable(orderedPrice, orderedCarts)
                            else -> false
                        }
                    }.map { Coupon(couponDetail = it) }

            _coupons.postValue(availableCoupons)
        }
    }
}
