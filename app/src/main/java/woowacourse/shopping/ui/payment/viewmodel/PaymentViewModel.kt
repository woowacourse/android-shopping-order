package woowacourse.shopping.ui.payment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.cart.CartWithProduct
import woowacourse.shopping.data.coupon.Coupon
import woowacourse.shopping.data.coupon.CouponRepository

class PaymentViewModel(
    private val orderedCartItemIds: List<Long>,
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val orderedProducts: MutableLiveData<List<CartWithProduct>> =
        MutableLiveData(emptyList())
    val orderAmount: LiveData<Int> =
        orderedProducts.map {
            it.sumOf { it.product.price * it.quantity.value }
        }

    private val _coupons: MutableLiveData<List<Coupon>> = MutableLiveData()
    val coupons: MediatorLiveData<List<Coupon>> =
        MediatorLiveData<List<Coupon>>().apply {
            addSource(_coupons) { value = availableCoupons() }
            addSource(orderedProducts) { value = availableCoupons() }
        }

    init {
        loadOrderedCartItems()
        loadCoupons()
    }

    private fun availableCoupons(): List<Coupon> {
        val orderAmount = orderedProducts.value?.sumOf { it.product.price * it.quantity.value } ?: 0
        val coupons = _coupons.value?.map { it.copy(orderAmount = orderAmount) } ?: emptyList()
        return coupons.filter { it.isValid() }
    }

    private fun loadOrderedCartItems() {
        val cartWithProducts = mutableListOf<CartWithProduct>()
        viewModelScope.launch {
            orderedCartItemIds.forEach {
                cartRepository.getCartItemByCartId(it).onSuccess {
                    cartWithProducts.add(it)
                    orderedProducts.value = cartWithProducts
                }
            }
        }
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            couponRepository.getCoupons().onSuccess {
                _coupons.value = it.map { it.copy(orderAmount = orderAmount.value ?: 0) }
            }
        }
    }
}
