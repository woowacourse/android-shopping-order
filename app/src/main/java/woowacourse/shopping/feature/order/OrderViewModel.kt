package woowacourse.shopping.feature.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.coupon.CouponRepository
import woowacourse.shopping.data.remote.order.OrderRepository
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

class OrderViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<OrderUistate>()
    val uiState: LiveData<OrderUistate> get() = _uiState

    private val _moveToMainEvent = MutableSingleLiveData(false)
    val moveToMainEvent: SingleLiveData<Boolean> get() = _moveToMainEvent

    fun fetchCoupon(carts: Carts) {
        viewModelScope.launch {
            couponRepository.getCoupons().onSuccess { result ->
            }
        }
    }
}
