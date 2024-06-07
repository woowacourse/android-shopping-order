package woowacourse.shopping.ui.coupon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.common.Event
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository

class CouponViewModel(
    private val selectedCartItemIds: List<Int>,
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _couponUiModels = MutableLiveData<List<CouponUiModel>>()
    val couponUiModels: LiveData<List<CouponUiModel>> get() = _couponUiModels

    private val _couponErrorEvent = MutableLiveData<Event<Unit>>()
    val couponErrorEvent: LiveData<Event<Unit>> get() = _couponErrorEvent

    init {
        if (selectedCartItemIds.isEmpty()) setError()
        loadCoupons()
    }

    private fun loadCoupons() =
        viewModelScope.launch {
            val selectedCartItems = selectedCartItemIds.toCartItems(this)
            couponRepository.findAll()
                .onSuccess {
                    val availableCoupons = it.filter { coupon -> coupon.available(selectedCartItems) }
                    _couponUiModels.value = availableCoupons.toCouponUiModels()
                }
                .onFailure { setError() }
        }

    private suspend fun List<Int>.toCartItems(scope: CoroutineScope): List<CartItem> {
        val cartItemsDeferred =
            scope.async {
                this@toCartItems.map { cartItemId ->
                    cartRepository.find(cartItemId).getOrNull()
                }
            }
        return cartItemsDeferred.await().filterNotNull()
    }

    private fun List<Coupon>.toCouponUiModels(): List<CouponUiModel> {
        return map { CouponUiModel.from(it) }
    }

    private fun setError() {
        _couponErrorEvent.value = Event(Unit)
    }
}
