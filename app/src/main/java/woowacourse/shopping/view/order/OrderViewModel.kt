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
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct

class OrderViewModel(
    private val couponRepository: CouponRepository = DefaultCouponRepository.get(),
    private val shoppingCartProductsToOrder: List<ShoppingCartProduct> = emptyList(),
) : ViewModel() {
    private val _orderState = MutableLiveData<OrderState>()
    val orderState: LiveData<OrderState> get() = _orderState

    private val _couponState = MutableLiveData<CouponState>()
    val couponState: LiveData<CouponState> get() = _couponState

    private val _event: MutableLiveData<OrderEvent> = MutableLiveData(OrderEvent.ORDER_PROCEEDING)
    val event: LiveData<OrderEvent> get() = _event

    private lateinit var allCoupons: List<Coupon>

    init {
        viewModelScope.launch {
            allCoupons = couponRepository.getAllCoupons()
        }
        _orderState.value =
            OrderState(
                totalPrice = shoppingCartProductsToOrder.sumOf { it.price },
                discountPrice = 5000,
                shippingFee = DEFAULT_SHIPPING_FEE,
                finalPrice = 202200,
            )
    }

    fun toggleCoupon() {
    }

    fun proceedOrder() {
        // TODO 장바구니 데이터 삭제 후 발생
        _event.value = OrderEvent.ORDER_SUCCESS
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
