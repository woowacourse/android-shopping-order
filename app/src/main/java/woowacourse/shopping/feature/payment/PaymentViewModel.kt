package woowacourse.shopping.feature.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.coupon.CouponRepository
import woowacourse.shopping.domain.model.Coupon
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

    init {
        getAllCoupons()
    }

    fun getAllCoupons() {
        viewModelScope.launch {
            val coupons = couponRepository.fetchAllCoupons().map { it.toDomain() }
            _coupons.postValue(coupons)
        }
    }

    fun setOrderDetails(orderIds: LongArray) {
        viewModelScope.launch {
            val allCarts = cartRepository.fetchAllCart().content
            val orderedCarts = allCarts.filter { it.id in orderIds }
            val orderedPrice = orderedCarts.sumOf { it.product.price * it.quantity }

            val newPrice =
                _price.value?.copy(orderPrice = orderedPrice, totalPrice = orderedPrice)
                    ?: Price(orderPrice = orderedPrice, totalPrice = orderedPrice)
            _price.postValue(newPrice)
        }
    }
}
