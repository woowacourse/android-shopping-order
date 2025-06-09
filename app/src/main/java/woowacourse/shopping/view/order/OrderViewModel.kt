package woowacourse.shopping.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.coupon.repository.CouponRepository
import woowacourse.shopping.data.coupon.repository.DefaultCouponRepository
import woowacourse.shopping.domain.order.Coupon
import woowacourse.shopping.domain.order.ShippingFee
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import java.time.LocalTime

class OrderViewModel(
    private val productsToOrder: List<ShoppingCartProduct>,
    private val couponRepository: CouponRepository = DefaultCouponRepository.get(),
) : ViewModel() {
    private val _coupons: MutableLiveData<List<CouponItem>> = MutableLiveData()
    val coupons: LiveData<List<CouponItem>> get() = _coupons

    private val _applyingCoupon: MutableLiveData<CouponItem> = MutableLiveData()
    val applyingCoupon: LiveData<CouponItem> get() = _applyingCoupon

    val price: Int get() = productsToOrder.sumOf { it.price }

    private val _shippingFee: MutableLiveData<ShippingFee> = MutableLiveData()
    val shippingFee: LiveData<ShippingFee> get() = _shippingFee

    init {
        getCoupons()
    }

    private fun getCoupons() {
        viewModelScope.launch {
            couponRepository
                .getCoupons()
                .onSuccess { coupons ->
                    handleAvailableCoupons(coupons)
                }
        }
    }

    private fun handleAvailableCoupons(coupons: List<Coupon>) {
        val availableCoupons =
            coupons.filter { coupon ->
                if (coupon.isExpiration) return@filter false

                when (coupon) {
                    is Coupon.PriceDiscount -> price >= coupon.minimumAmount
                    is Coupon.FreeShipping -> price >= coupon.minimumAmount
                    is Coupon.PercentageDiscount -> {
                        val now = LocalTime.now()
                        now in coupon.availableStartTime..coupon.availableEndTime
                    }

                    is Coupon.Bonus -> {
                        productsToOrder.any { it.quantity >= coupon.buyQuantity + coupon.getQuantity }
                    }
                }
            }

        _coupons.value = availableCoupons.map { it.toUiModel() }
    }

    fun updateApplyingCoupon(couponId: Int) {
        val couponToApply: CouponItem = coupons.value?.find { it.id == couponId } ?: return
        _applyingCoupon.value = couponToApply
        updateCouponSelected(couponToApply)
    }

    private fun updateCouponSelected(couponToApply: CouponItem) {
        val currentCoupons = _coupons.value.orEmpty()

        val updatedCoupons =
            currentCoupons.map {
                if (it.id == couponToApply.id) {
                    it.copy(isSelected = true)
                } else {
                    it.copy(isSelected = false)
                }
            }

        _coupons.value = updatedCoupons
    }

    companion object {
        fun provideFactory(productsToOrder: Array<ShoppingCartProduct>): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    OrderViewModel(
                        productsToOrder.toList(),
                    ) as T
            }
    }
}
