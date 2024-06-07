package woowacourse.shopping.ui.payment.viewmodel

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
    private val _coupons: MutableLiveData<List<Coupon>> = MutableLiveData()
    val coupons: MutableLiveData<List<Coupon>> = _coupons

    private val orderedProducts: MutableLiveData<List<CartWithProduct>> = MutableLiveData()
    val orderAmount =
        orderedProducts.map {
            it.sumOf { it.product.price * it.quantity.value }
        }

    init {
        loadCoupons()
        loadOrderedCartItems()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            couponRepository.getCoupons().onSuccess {
                _coupons.value = it
            }
        }
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
}
