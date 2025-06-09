package woowacourse.shopping.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.data.coupon.repository.CouponRepository
import woowacourse.shopping.data.coupon.repository.DefaultCouponRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.Coupons
import woowacourse.shopping.domain.coupon.FreeShipping
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct

class OrderViewModel(
    private val couponRepository: CouponRepository = DefaultCouponRepository.get(),
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.get(),
    private val shoppingCartProductsToOrder: List<ShoppingCartProduct> = emptyList(),
) : ViewModel() {
    private val coupons: MutableLiveData<Coupons> = MutableLiveData()

    private val _couponState: MutableLiveData<List<CouponState>> =
        MediatorLiveData<List<CouponState>>().apply {
            addSource(coupons) { coupons ->
                value =
                    coupons.available(shoppingCartProductsToOrder)
                        .map { CouponState(it, false) }
            }
        }

    val couponState: LiveData<List<CouponState>> get() = _couponState

    private val handler =
        CoroutineExceptionHandler { _, exception ->
            _event.value = OrderEvent.FAIL_TO_LOAD_COUPONS
        }

    val orderState: LiveData<OrderState> =
        couponState.map { couponState ->
            val coupon =
                couponState
                    .filter { it.isSelected }
                    .map { coupons.value?.get(it.id) }

            val totalPrice = shoppingCartProductsToOrder.sumOf { it.price }
            val totalShippingDiscount =
                coupon.totalShippingDiscount(shoppingCartProductsToOrder)
            val totalDiscount = coupon.totalDiscount(shoppingCartProductsToOrder)

            OrderState(
                totalPrice = totalPrice,
                discountPrice = totalDiscount,
                shippingFee = totalShippingDiscount,
                finalPrice = totalPrice + totalShippingDiscount - totalDiscount,
            )
        }

    private val _event: MutableLiveData<OrderEvent> = MutableLiveData(OrderEvent.ORDER_PROCEEDING)
    val event: LiveData<OrderEvent> get() = _event

    init {
        viewModelScope.launch(handler) {
            coupons.value = Coupons(couponRepository.getAllCoupons().getOrThrow())
        }
    }

    fun toggleCoupon(couponState: CouponState) {
        _couponState.value =
            _couponState.value?.map {
                it.copy(isSelected = (it.id == couponState.id))
            }
    }

    fun proceedOrder() {
        viewModelScope.launch(handler) {
            _event.value = OrderEvent.ORDER_SUCCESS
        }
    }

    fun removeShoppingCartProducts() {
        viewModelScope.launch(handler) {
            shoppingCartProductsToOrder.forEach {
                shoppingCartRepository.remove(it.id)
            }
        }
    }

    private fun List<Coupon?>.totalDiscount(shoppingCartProductsToOrder: List<ShoppingCartProduct>): Int {
        return sumOf { it?.discountAmount(shoppingCartProductsToOrder) ?: 0 }
    }

    private fun List<Coupon?>.totalShippingDiscount(shoppingCartProductsToOrder: List<ShoppingCartProduct>): Int {
        return Coupon.DEFAULT_SHIPPING_FEE -
            this
                .filterIsInstance<FreeShipping>()
                .sumOf { it.discountAmount(shoppingCartProductsToOrder) }
    }

    companion object {
        fun factory(shoppingCartProductsToOrder: List<ShoppingCartProduct>): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    OrderViewModel(
                        shoppingCartProductsToOrder = shoppingCartProductsToOrder,
                    )
                }
            }
    }
}
