package woowacourse.shopping.ui.coupon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
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
) : ViewModel(), CouponListener {
    private val coupons = MutableLiveData<List<Coupon>>()

    private val _couponUiModels = MutableLiveData<CouponUiModels>()
    val couponUiModels: LiveData<CouponUiModels> get() = _couponUiModels

    val isEmptyCoupon = coupons.map { it.isEmpty() }

    private val _couponErrorEvent = MutableLiveData<Event<Unit>>()
    val couponErrorEvent: LiveData<Event<Unit>> get() = _couponErrorEvent

    private val selectedCartItems = MutableLiveData<List<CartItem>>()

    val orderPrice: LiveData<Int> =
        selectedCartItems.map { it.sumOf { cartItem -> cartItem.totalPrice() } }

    private val _discountPrice = MutableLiveData<Int>(0)
    val discountPrice: LiveData<Int> get() = _discountPrice

    val totalOrderPrice = MediatorLiveData<Int>(0)

    init {
        if (selectedCartItemIds.isEmpty()) setError()
        loadCoupons()
        totalOrderPrice.addSource(_discountPrice) {
            totalOrderPrice.value = (orderPrice.value ?: 0) + it + Coupon.DELIVERY_FEE
        }
        totalOrderPrice.addSource(orderPrice) {
            totalOrderPrice.value = it + (_discountPrice.value ?: 0) + Coupon.DELIVERY_FEE
        }
    }

    private fun loadCoupons() =
        viewModelScope.launch {
            val selectedCartItems = selectedCartItemIds.toCartItems(this)
            this@CouponViewModel.selectedCartItems.value = selectedCartItems
            couponRepository.findAll()
                .onSuccess {
                    val availableCoupons =
                        it.filter { coupon -> coupon.available(selectedCartItems) }
                    coupons.value = availableCoupons
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

    private fun List<Coupon>.toCouponUiModels(): CouponUiModels {
        val uiModels = map { CouponUiModel.from(it) }
        return CouponUiModels(uiModels)
    }

    private fun setError() {
        _couponErrorEvent.value = Event(Unit)
    }

    override fun selectCoupon(couponId: Int) {
        val couponUiModels = couponUiModels()
        if (couponUiModels.isSelect(couponId)) {
            _couponUiModels.value = couponUiModels().unselectCoupon(couponId)
            _discountPrice.value = 0
            return
        }
        _couponUiModels.value = couponUiModels().selectCoupon(couponId)
        val coupon = findCoupon(couponId) ?: return
        _discountPrice.value = coupon.discountPrice(selectedCartItems()) * -1
    }

    private fun couponUiModels(): CouponUiModels = _couponUiModels.value ?: CouponUiModels()

    private fun selectedCartItems(): List<CartItem> = selectedCartItems.value ?: emptyList()

    private fun findCoupon(couponId: Int): Coupon? {
        val coupons = this.coupons.value ?: return null
        return coupons.find { it.id == couponId }
    }
}
