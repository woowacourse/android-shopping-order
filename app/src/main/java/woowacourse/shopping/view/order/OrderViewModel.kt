package woowacourse.shopping.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.data.coupon.remote.repository.CouponRepository
import woowacourse.shopping.data.coupon.remote.repository.DefaultCouponRepository
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
    private lateinit var coupons: Coupons

    private val _couponState = MutableLiveData<List<CouponState>>()
    val couponState: LiveData<List<CouponState>> get() = _couponState

    private val handler =
        CoroutineExceptionHandler { _, exception ->
            _event.value = OrderEvent.FAIL_TO_LOAD_COUPONS
        }

    val orderState: LiveData<OrderState>
        get() =
            couponState.map { couponState ->
                val coupon =
                    couponState
                        .filter { it.isSelected }
                        .map { coupons[it.id] }

                val totalPrice = shoppingCartProductsToOrder.sumOf { it.price }
                val totalShippingDiscount =
                    Coupon.DEFAULT_SHIPPING_FEE -
                        coupon
                            .filterIsInstance<FreeShipping>()
                            .sumOf { it.disCountAmount(shoppingCartProductsToOrder) }

                val totalDiscount =
                    coupon
                        .sumOf { it?.disCountAmount(shoppingCartProductsToOrder) ?: 0 }

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
            coupons = Coupons(couponRepository.getAllCoupons().getOrThrow())
            _couponState.value =
                coupons.available(shoppingCartProductsToOrder).map {
                    CouponState(
                        id = it.id,
                        isSelected = false,
                        title = it.description,
                        expirationDate = it.explanationDate,
                        minimumOrderPrice = it.minimumAmount,
                    )
                }
        }
    }

    fun toggleCoupon(couponState: CouponState) {
        _couponState.value =
            _couponState.value
                ?.map {
                    it.copy(isSelected = false)
                }
                ?.map {
                    if (it.id == couponState.id) {
                        it.copy(
                            isSelected = true,
                        )
                    } else {
                        it
                    }
                }
    }

    fun proceedOrder() {
        viewModelScope.launch(handler) {
            shoppingCartProductsToOrder.forEach {
                shoppingCartRepository.remove(it.id)
            }
            _event.value = OrderEvent.ORDER_SUCCESS
        }
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
