package woowacourse.shopping.feature.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.coupon.CouponRepository
import woowacourse.shopping.data.remote.order.OrderRepository
import woowacourse.shopping.feature.cart.adapter.CartGoodsItem
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import java.time.LocalDateTime

class OrderViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<OrderUiState>()
    val uiState: LiveData<OrderUiState> get() = _uiState

    private val _moveToMainEvent = MutableSingleLiveData(false)
    val moveToMainEvent: SingleLiveData<Boolean> get() = _moveToMainEvent

    fun fetchCoupons(order: List<CartGoodsItem>) {
        viewModelScope.launch {
            couponRepository
                .getCoupons()
                .onSuccess { result ->
                    val now = LocalDateTime.now()
                    val carts = order.map { it.cart }
                    val payment = order.sumOf { it.cart.product.price * it.cart.quantity }

                    val coupons = result.filter { it.isUsable(now, carts, payment) }
                    _uiState.value = OrderUiState.of(order, coupons, DEFAULT_DELIVERY_FEE)
                }
        }
    }

    fun sendOrder() {
        viewModelScope.launch {
            orderRepository
                .fetchOrder(_uiState.value?.orderCartIds ?: emptyList())
            // .onSuccess { _uiEvent.setValue(OrderUiEvent.OrderComplete) }
        }
    }

    private fun changeCouponCheckState(couponId: Int) {
        _uiState.value = _uiState.value?.changeCouponCheckState(couponId)
    }

    val couponHandler =
        object : CouponClickListener {
            override fun onCouponClick(couponId: Int) {
                changeCouponCheckState(couponId)
            }
        }

    companion object {
        private const val DEFAULT_DELIVERY_FEE = 3000
    }
}
