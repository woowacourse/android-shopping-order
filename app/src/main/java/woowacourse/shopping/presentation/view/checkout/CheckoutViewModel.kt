package woowacourse.shopping.presentation.view.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.RepositoryProvider
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.coupon.CouponRepository
import woowacourse.shopping.data.repository.order.OrderRepository
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Price
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.toUiModel
import java.time.LocalDateTime

class CheckoutViewModel(
    private val selectedProductIds: List<Long>,
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _coupons = MutableLiveData<List<CouponUiModel>>()
    val coupons: LiveData<List<CouponUiModel>> = _coupons

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    val totalPrice: LiveData<Long> =
        _cartItems.map { cartItems ->
            cartItems.sumOf { cartItem -> cartItem.totalPrice }
        }

    val shippingFee = Price.SHIPPING_FEE

    val discountAmount: LiveData<Long> =
        _coupons.switchMap { coupons ->
            _cartItems.map { cartItems ->
                val selectedCoupon = coupons.firstOrNull { coupon -> coupon.isSelected }
                selectedCoupon?.coupon?.discountAmount(cartItems) ?: 0
            }
        }

    val grandTotal: LiveData<Long> =
        totalPrice.switchMap { totalPrice ->
            discountAmount.map { discountAmount ->
                (totalPrice - discountAmount + shippingFee).coerceAtLeast(0)
            }
        }

    init {
        viewModelScope.launch {
            loadSelectedCartItems()
            loadCoupons()
        }
    }

    private suspend fun loadSelectedCartItems() {
        cartRepository.loadCartItemsByProductIds(selectedProductIds).let { selectedCartItems ->
            _cartItems.postValue(selectedCartItems)
        }
    }

    private suspend fun loadCoupons() {
        val availableCoupons =
            couponRepository.loadCoupons()
                .filter { coupon ->
                    coupon.isAvailable(_cartItems.value.orEmpty(), LocalDateTime.now())
                }
                .map(Coupon::toUiModel)
        _coupons.value = availableCoupons
    }

    fun toggleCouponSelection(target: CouponUiModel) {
        _coupons.value =
            _coupons.value?.map { coupon ->
                if (coupon.coupon.info.id == target.coupon.info.id) {
                    coupon.copy(isSelected = !target.isSelected)
                } else {
                    coupon.copy(isSelected = false)
                }
            }
    }

    fun finalizeOrder() {
        viewModelScope.launch {
            orderRepository.placeOrder(_cartItems.value.orEmpty().map(CartItem::cartId))
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun factory(selectedProductIds: List<Long>): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val cartRepository = RepositoryProvider.cartRepository
                    val couponRepository = RepositoryProvider.couponRepository
                    val orderRepository = RepositoryProvider.orderRepository
                    return CheckoutViewModel(
                        selectedProductIds,
                        cartRepository,
                        couponRepository,
                        orderRepository,
                    ) as T
                }
            }
    }
}
