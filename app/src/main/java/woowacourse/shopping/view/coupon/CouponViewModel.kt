package woowacourse.shopping.view.coupon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponCalculator
import woowacourse.shopping.domain.model.coupon.SelectCouponResult
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.recommend.CouponEvent
import woowacourse.shopping.view.recommend.RecommendEvent

class CouponViewModel(
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModel(), OnClickCoupon {
    private var shoppingCart: ShoppingCart = ShoppingCart()

    val couponCalculator = CouponCalculator()

    private val _couponEvent: MutableLiveData<CouponEvent> = MutableLiveData()
    val couponEvent: LiveData<CouponEvent> get() = _couponEvent
    private val _errorEvent: MutableLiveData<RecommendEvent> = MutableLiveData()
    val errorEvent: LiveData<RecommendEvent> get() = _errorEvent
    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice
    val deliveryCharge = 3000

    fun loadCoupons() =
        viewModelScope.launch {
            couponRepository.loadCoupons()
                .onSuccess { coupons ->
                    couponCalculator.loadCoupons(coupons)
                }.onFailure {
                    _errorEvent.postValue(RecommendEvent.ErrorEvent.NotKnownError)
                }
        }

    private fun orderItems() =
        viewModelScope.launch {
            val ids = shoppingCart.cartItems.value?.map { it.id.toInt() }
            orderRepository.orderShoppingCart(ids ?: throw NoSuchDataException())
                .onSuccess {
                    _couponEvent.setValue(
                        CouponEvent.OrderRecommends.Success,
                    )
                }
                .onFailure {
                    when (it) {
                        is CouponEvent.ErrorEvent ->
                            _couponEvent.postValue(it)

                        else ->
                            _errorEvent.postValue(RecommendEvent.ErrorEvent.NotKnownError)
                    }
                }
        }

    override fun clickCoupon(coupon: Coupon) {
        val selectCouponResult =
            couponCalculator.selectCoupon(
                coupon = coupon,
                shoppingCart = shoppingCart,
                deliveryCharge = deliveryCharge,
            )
        when (selectCouponResult) {
            SelectCouponResult.InValidCount -> _couponEvent.setValue(CouponEvent.SelectCoupon.InvalidCount)
            SelectCouponResult.InValidDate -> _couponEvent.setValue(CouponEvent.SelectCoupon.InvalidDate)
            SelectCouponResult.InValidPrice -> _couponEvent.setValue(CouponEvent.SelectCoupon.InvalidPrice)
            SelectCouponResult.Valid -> _couponEvent.setValue(CouponEvent.SelectCoupon.Success)
        }
    }

    override fun clickPayment() {
        orderItems()
    }

    fun saveCheckedShoppingCarts(shoppingCart: ShoppingCart) {
        this.shoppingCart = shoppingCart
        _totalPrice.value = shoppingCart.getTotalPrice()
    }
}
