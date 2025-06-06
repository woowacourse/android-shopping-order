package woowacourse.shopping.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import woowacourse.shopping.data.coupon.remote.repository.CouponRepository
import woowacourse.shopping.data.coupon.remote.repository.DefaultCouponRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.coupon.Coupons
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct

class OrderViewModel(
    private val couponRepository: CouponRepository = DefaultCouponRepository.get(),
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.get(),
    private val shoppingCartProductsToOrder: List<ShoppingCartProduct> = emptyList(),
) : ViewModel() {
    private val _orderState = MutableLiveData<OrderState>()
    val orderState: LiveData<OrderState> get() = _orderState

    private val _couponState = MutableLiveData<List<CouponState>>()
    val couponState: LiveData<List<CouponState>> get() = _couponState

    private val _event: MutableLiveData<OrderEvent> = MutableLiveData(OrderEvent.ORDER_PROCEEDING)
    val event: LiveData<OrderEvent> get() = _event

    private lateinit var coupons: Coupons

    init {
        viewModelScope.launch {
            coupons = Coupons(couponRepository.getAllCoupons())
            _orderState.value =
                OrderState(
                    totalPrice = shoppingCartProductsToOrder.sumOf { it.price },
                    discountPrice = 5000,
                    shippingFee = DEFAULT_SHIPPING_FEE,
                    finalPrice = 202200,
                )
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
        viewModelScope.launch {
            shoppingCartProductsToOrder.forEach {
                shoppingCartRepository.remove(it.id)
            }
            _event.value = OrderEvent.ORDER_SUCCESS
        }
    }

    companion object {
        private const val DEFAULT_SHIPPING_FEE = 3000

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
