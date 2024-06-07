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
import woowacourse.shopping.data.coupon.Bogo
import woowacourse.shopping.data.coupon.CouponRepository
import woowacourse.shopping.data.coupon.CouponState
import woowacourse.shopping.data.coupon.Fixed5000
import woowacourse.shopping.data.coupon.Freeshipping
import woowacourse.shopping.data.coupon.MiracleSale

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

    private val _coupons: MutableLiveData<List<CouponState>> = MutableLiveData()
    val coupons: MediatorLiveData<List<CouponState>> =
        MediatorLiveData<List<CouponState>>().apply {
            addSource(_coupons) { value = availableCoupons() }
            addSource(orderedProducts) { value = availableCoupons() }
        }

    init {
        loadOrderedCartItems()
        loadCoupons()
    }

    private fun availableCoupons(): List<CouponState> {
        val orderAmount = orderedProducts.value?.sumOf { it.product.price * it.quantity.value } ?: 0
        val coupons =
            _coupons.value?.map {
                couponState(it, orderAmount)
            } ?: emptyList()
        return coupons.filter { it.isValid() }
    }

    private fun couponState(
        couponState: CouponState,
        orderAmount: Int,
    ) = when (couponState) {
        is Fixed5000 -> couponState.copy(orderAmount = orderAmount)
        is Freeshipping -> couponState.copy(orderAmount = orderAmount)
        is MiracleSale -> couponState.copy(orderAmount = orderAmount)
        is Bogo -> couponState.copy(orderedProduct = orderedProducts.value ?: emptyList())
        else -> throw IllegalStateException()
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
                _coupons.value = it
            }
        }
    }

    companion object {
        const val DELIVERY_AMOUNT = 3000
    }
}
