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
    private var currentCoupon: Coupon? = null

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

        applyCoupon(selectedCoupon)
    }

    fun applyCoupon(selectedCoupon: Coupon) {
        val currentPrice = _price.value ?: Price()

        if (currentCoupon?.couponDetail?.code == selectedCoupon.couponDetail.code) {
            _price.value =
                currentPrice.copy(
                    discountPrice = 0,
                    shippingFee = 3000,
                    totalPrice = currentPrice.orderPrice + 3000,
                )
            currentCoupon = null
            return
        }

        val contract = CouponContract.getContract(selectedCoupon.couponDetail.discountType)
        val newPrice = contract.apply(currentPrice, selectedCoupon, orderedCarts)

        _price.value = newPrice
        currentCoupon = selectedCoupon
    }

    private fun getAvailableCoupons() {
        viewModelScope.launch {
            val allCoupons = couponRepository.fetchAllCoupons().map { it.toDomain() }
            val orderedPrice = _price.value?.orderPrice ?: 0

            val availableCoupons =
                allCoupons
                    .filter { couponDetail ->
                        val contract = CouponContract.getContract(couponDetail.discountType)
                        contract.isAvailable(orderedPrice, orderedCarts) == true
                    }.map { Coupon(couponDetail = it) }

            _coupons.postValue(availableCoupons)
        }
    }
}
