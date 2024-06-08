package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.coupon.CouponRepository
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.domain.ProductListItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository

class PaymentViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _coupon = MutableLiveData<List<Coupon>>()
    val coupon: LiveData<List<Coupon>> get() = _coupon

    private val carts = MutableLiveData<List<ProductListItem.ShoppingProductItem>>()

    private val selectedCoupon: LiveData<Coupon> =
        _coupon.map { coupons ->
            coupons[0]
        }

    val totalPrice: LiveData<Long> =
        carts.map { shoppingProducts ->
            shoppingProducts.sumOf { it.quantity * it.price }
        }

    val discount: MediatorLiveData<Long> = MediatorLiveData()

    val discountedPrice: MediatorLiveData<Long> = MediatorLiveData()

    init {
        initDiscountMediateLivedata()
        initDiscountedPriceMediateLivedata()
    }

    private fun initDiscountMediateLivedata() {
        discount.apply {
            addSource(carts) {
                value = checkDiscount(carts, selectedCoupon)
            }
            addSource(selectedCoupon) {
                value = checkDiscount(carts, selectedCoupon)
            }
        }
    }

    private fun initDiscountedPriceMediateLivedata() {
        discountedPrice.apply {
            addSource(totalPrice) {
                value = checkDiscountedPrice(totalPrice, discount)
            }
            addSource(discount) {
                value = checkDiscountedPrice(totalPrice, discount)
            }
        }
    }

    private fun checkDiscount(
        carts: LiveData<List<ProductListItem.ShoppingProductItem>>,
        selectedCoupon: LiveData<Coupon>,
    ): Long {
        return carts.value?.let { selectedCoupon.value?.calculateDiscount(it) ?: 0L } ?: 0L
    }

    private fun checkDiscountedPrice(
        totalPrice: LiveData<Long>,
        discount: MediatorLiveData<Long>,
    ): Long {
        return totalPrice.value?.let { totalPrice ->
            discount.value?.let { discountPrice ->
                totalPrice - discountPrice
            }
        } ?: 0
    }

    fun fetchInitialData() {
        fetchCoupon()
        fetchCarts()
    }

    private fun fetchCoupon() {
        viewModelScope.launch {
            couponRepository.requestCoupons().onSuccess {
                _coupon.value = it
            }.onFailure {
                // TODO
            }
        }
    }

    private fun fetchCarts() {
        viewModelScope.launch {
            cartRepository.loadAll().onSuccess {
                carts.value = it.map { cart -> cart.toShoppingProduct() }
            }.onFailure {
                // TODO
            }
        }
    }
}
