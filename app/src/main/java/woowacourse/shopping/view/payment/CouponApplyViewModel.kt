package woowacourse.shopping.view.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.payment.CouponEvent
import woowacourse.shopping.data.payment.DefaultCouponRepository
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.payment.Coupon
import woowacourse.shopping.domain.payment.CouponRepository
import woowacourse.shopping.domain.payment.DefaultDeliveryFee
import woowacourse.shopping.domain.payment.DeliveryFee
import woowacourse.shopping.view.MutableSingleLiveData
import woowacourse.shopping.view.SingleLiveData

class CouponApplyViewModel(
    private val repository: CouponRepository = DefaultCouponRepository(),
    private val deliveryFee: DeliveryFee = DefaultDeliveryFee(),
) : ViewModel() {
    private val _event: MutableSingleLiveData<CouponEvent> = MutableSingleLiveData()
    val event: SingleLiveData<CouponEvent> get() = _event

    var cartItems: List<CartItem> = emptyList()

    private val _state: MutableLiveData<CouponApplyState> =
        MutableLiveData(CouponApplyState.Loading)
    val state: LiveData<CouponApplyState> get() = _state

    fun loadCouponsRepository() {
        viewModelScope.launch {
            repository
                .loadCoupons()
                .onSuccess { coupons: List<Coupon> ->
                    _state.value =
                        state.value?.copy(
                            loading = false,
                            coupons =
                                listOf(CouponsItem.Header) +
                                    coupons.map { coupon: Coupon ->
                                        CouponsItem.Coupon(coupon, false)
                                    },
                            selectedCoupon = null,
                            cartItems = cartItems,
                            deliveryFee = deliveryFee.value,
                        )
                }.onFailure {
                    _event.value = CouponEvent.FETCH_COUPONS_FAILURE
                }
        }
    }

    fun selectCoupon(coupon: CouponsItem.Coupon) {
        val oldCoupons = state.value?.coupons ?: emptyList()
        val newCoupons: List<CouponsItem>
        if (coupon.selected) {
            newCoupons =
                oldCoupons.map { couponItem: CouponsItem ->
                    if (couponItem is CouponsItem.Coupon) {
                        couponItem.copy(selected = false)
                    } else {
                        couponItem
                    }
                }
        } else {
            newCoupons =
                oldCoupons.map { couponItem: CouponsItem ->
                    if (couponItem is CouponsItem.Coupon) {
                        couponItem.copy(selected = couponItem.value == coupon)
                    } else {
                        couponItem
                    }
                }
        }

        _state.value =
            state.value?.copy(
                coupons = newCoupons,
                selectedCoupon = coupon.value,
            )
    }
}
